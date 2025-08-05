package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.ERROR;
import static com.github.toastshaman.tinytypes.events.EventFilters.*;
import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;
import static org.assertj.core.api.Assertions.as;
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
