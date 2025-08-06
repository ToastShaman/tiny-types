package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.ERROR;
import static com.github.toastshaman.tinytypes.events.EventFilters.*;
import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class EventFiltersTest {

    @Test
    void can_add_service_version_metadata() {
        var recording = new RecordingEvents();
        var events = AddServiceVersion("1.2.3").then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service_version", "1.2.3");
    }

    @Test
    void can_add_build_id_metadata() {
        var recording = new RecordingEvents();
        var events = AddBuildId("build-456").then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service_build_id", "build-456");
    }

    @Test
    void can_add_git_hash_metadata() {
        var recording = new RecordingEvents();
        var events = AddGitHash("abc123def456").then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service_build_git_hash", "abc123def456");
    }

    @Test
    void can_add_deployment_at_metadata() {
        var deploymentTime = Instant.parse("2023-01-01T12:00:00Z");
        var recording = new RecordingEvents();
        var events = AddDeploymentAt(deploymentTime).then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service_build_deployment_at", deploymentTime);
    }

    @Test
    void can_add_deployment_age_metadata() {
        var deployedAt = Instant.parse("2023-01-01T12:00:00Z");
        var currentTime = Instant.parse("2023-01-01T13:30:00Z");
        var fixedClock = Clock.fixed(currentTime, ZoneOffset.UTC);

        var recording = new RecordingEvents();
        var events = AddDeploymentAge(deployedAt, fixedClock).then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service_build_deployment_age_minutes", 90L); // 1.5 hours = 90 minutes
    }

    @Test
    void can_add_uptime_metadata() {
        var currentTime = Instant.parse("2023-01-01T12:01:00Z");
        var fixedClock = Clock.fixed(currentTime, ZoneOffset.UTC);

        var recording = new RecordingEvents();
        var events = AddUptime(fixedClock).then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsKeys("uptime_sec", "uptime_sec_log_10");
    }

    @Test
    void can_add_environment_name_metadata() {
        var recording = new RecordingEvents();
        var events = AddEnvironmentName("production").then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("environment_name", "production");
    }

    @Test
    void can_add_team_name_metadata() {
        var recording = new RecordingEvents();
        var events = AddTeamName("platform-team").then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("team_name", "platform-team");
    }

    @Test
    void can_add_cloud_region_metadata() {
        var recording = new RecordingEvents();
        var events = AddCloudRegion("us-east-1").then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("cloud_region", "us-east-1");
    }

    @Test
    void can_add_correlation_id_metadata() {
        var recording = new RecordingEvents();
        var correlationId = UUID.randomUUID().toString();
        var events = AddCorrelationId(event -> correlationId).then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("correlation_id", correlationId);
    }

    @Test
    void can_filter_error_events() {
        var recording = new RecordingEvents();
        var events = Accept(ERROR).then(recording);

        events.record(MyEvent.random());
        events.record(MyErrorEvent.random());

        assertThatEvents(recording).doesNotContain(MyEvent.class);
        assertThatEvents(recording).containsSingle(MyErrorEvent.class);
    }

    @Test
    void can_reject_error_events() {
        var recording = new RecordingEvents();
        var events = Reject(ERROR).then(recording);

        events.record(MyEvent.random());
        events.record(MyErrorEvent.random());

        assertThatEvents(recording).containsSingle(MyEvent.class);
        assertThatEvents(recording).doesNotContain(MyErrorEvent.class);
    }

    @Test
    void can_chain_multiple_metadata_filters() {
        var recording = new RecordingEvents();
        var events = AddServiceVersion("2.0.0")
                .then(AddBuildId("build-789"))
                .then(AddEnvironmentName("staging"))
                .then(AddTeamName("backend-team"))
                .then(AddCloudRegion("eu-west-1"))
                .then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service_version", "2.0.0")
                .containsEntry("service_build_id", "build-789")
                .containsEntry("environment_name", "staging")
                .containsEntry("team_name", "backend-team")
                .containsEntry("cloud_region", "eu-west-1");
    }

    @Test
    void sampling_rejects_invalid_probability_values() {
        assertThatThrownBy(() -> Sampling(-0.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Probability must be in range [0, 1]");

        assertThatThrownBy(() -> Sampling(1.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Probability must be in range [0, 1]");
    }

    @Test
    void sampling_accepts_valid_probability_values() {
        // These should not throw exceptions
        Sampling(0.0);
        Sampling(0.5);
        Sampling(1.0);
    }

    @Test
    void sampling_always_accepts_error_events() {
        var recording = new RecordingEvents();
        var events = Sampling(0.0).then(recording); // 0% probability for non-error events

        // Record multiple error events - they should all be accepted
        for (int i = 0; i < 10; i++) {
            events.record(MyErrorEvent.random());
        }

        assertThatEvents(recording).hasSize(10);
    }

    @Test
    void sampling_always_accepts_warn_events() {
        var recording = new RecordingEvents();
        var events = Sampling(0.0).then(recording); // 0% probability for non-warn events

        // Record multiple warn events - they should all be accepted
        for (int i = 0; i < 10; i++) {
            events.record(MyWarnEvent.random());
        }

        assertThatEvents(recording).hasSize(10);
    }

    @Test
    void sampling_with_zero_probability_rejects_non_error_warn_events() {
        var recording = new RecordingEvents();
        var events = Sampling(0.0).then(recording);

        // Record multiple info events - they should all be rejected
        for (int i = 0; i < 10; i++) {
            events.record(MyEvent.random());
        }

        assertThatEvents(recording).isEmpty();
    }

    @Test
    void sampling_with_full_probability_accepts_all_events() {
        var recording = new RecordingEvents();
        var events = Sampling(1.0).then(recording);

        events.record(MyEvent.random());
        events.record(MyErrorEvent.random());
        events.record(MyWarnEvent.random());

        assertThatEvents(recording).hasSize(3);
    }

    @Test
    void sampling_with_partial_probability_accepts_error_warn_and_some_others() {
        var recording = new RecordingEvents(1000);
        var events = Sampling(0.5).then(recording);

        // Add error and warn events - should always be accepted
        events.record(MyErrorEvent.random());
        events.record(MyWarnEvent.random());

        // Add many info events - roughly half should be accepted due to probability
        for (int i = 0; i < 1000; i++) {
            events.record(MyEvent.random());
        }

        // Should have at least the 2 error/warn events, plus some info events
        assertThatEvents(recording).hasSizeGreaterThanOrEqualTo(2);
        assertThatEvents(recording).contains(MyErrorEvent.class);
        assertThatEvents(recording).contains(MyWarnEvent.class);

        // With 1000 trials at 50% probability, we should get roughly 500 info events
        // Allow for some variance (between 400-600)
        var infoEventCount = recording.captured.stream()
                .mapToInt(event -> event instanceof MyEvent ? 1 : 0)
                .sum();

        assertThat(infoEventCount).isBetween(400, 600);
    }

    private record MyEvent(UUID id) implements Event {
        public static MyEvent random() {
            return new MyEvent(UUID.randomUUID());
        }
    }

    private record MyWarnEvent(UUID id) implements Event {
        @Override
        public EventCategory category() {
            return EventCategory.WARN;
        }

        public static MyWarnEvent random() {
            return new MyWarnEvent(UUID.randomUUID());
        }
    }

    private record MyErrorEvent(UUID id) implements Event {

        @Override
        public EventCategory category() {
            return ERROR;
        }

        public static MyErrorEvent random() {
            return new MyErrorEvent(UUID.randomUUID());
        }
    }
}
