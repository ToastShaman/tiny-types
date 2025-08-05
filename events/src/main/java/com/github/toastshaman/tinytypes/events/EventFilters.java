package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.ERROR;
import static com.github.toastshaman.tinytypes.events.EventCategory.WARN;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.MDC;

public final class EventFilters {

    private EventFilters() {}

    public static EventFilter AddEventName = next -> event -> {
        var aClass = event instanceof MetadataEvent m ? m.event().getClass() : event.getClass();
        var eventWithName = event.addMetadata("event_name", aClass.getSimpleName());
        next.record(eventWithName);
    };

    public static EventFilter AddServiceName(String value) {
        return AddStatic("service_name", value);
    }

    public static EventFilter AddServiceVersion(String value) {
        return AddStatic("service_version", value);
    }

    public static EventFilter AddBuildId(String value) {
        return AddStatic("service_build_id", value);
    }

    public static EventFilter AddGitHash(String value) {
        return AddStatic("service_build_git_hash", value);
    }

    public static EventFilter AddDeploymentAt(Instant value) {
        return AddStatic("service_build_deployment_at", value);
    }

    public static EventFilter AddDeploymentAge(Instant deployedAt, Clock clock) {
        return next -> event -> next.record(event.addMetadata(
                "service_build_deployment_age_minutes",
                Duration.between(deployedAt, Instant.now(clock)).toMinutes()));
    }

    public static EventFilter AddUptime(Clock clock) {
        var startedAt = Instant.now(clock);

        return next -> event -> {
            var uptimeInSeconds =
                    Duration.between(startedAt, Instant.now(clock)).toSeconds();

            var eventWithMetadata = event.addMetadata("uptime_sec", uptimeInSeconds)
                    .addMetadata("uptime_sec_log_10", Math.log10(uptimeInSeconds));

            next.record(eventWithMetadata);
        };
    }

    public static EventFilter AddEnvironmentName(String value) {
        return AddStatic("environment_name", value);
    }

    public static EventFilter AddTeamName(String value) {
        return AddStatic("team_name", value);
    }

    public static EventFilter AddCloudRegion(String value) {
        return AddStatic("cloud_region", value);
    }

    public static EventFilter AddCorrelationId(Supplier<String> id) {
        return AddStatic("correlation_id", id.get());
    }

    private static EventFilter AddStatic(String key, Object value) {
        return next -> event -> next.record(event.addMetadata(key, value));
    }

    public static EventFilter AddTimestamp() {
        return AddTimestamp(Clock.systemUTC());
    }

    public static EventFilter AddTimestamp(Clock clock) {
        return next -> event -> next.record(event.addMetadata("timestamp", clock.instant()));
    }

    public static EventFilter AddMDCContext() {
        return next -> event -> MDC.getCopyOfContextMap().forEach(event::addMetadata);
    }

    public static EventFilter Sampling(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("Probability must be in range [0, 1]");
        }
        return Accept(has(ERROR).or(has(WARN)).or(e -> Math.random() < probability));
    }

    public static EventFilter Reject(EventCategory... categories) {
        return Reject(Arrays.stream(categories)
                .map(EventFilters::has)
                .reduce(Predicate::or)
                .orElse(it -> false));
    }

    public static EventFilter Accept(EventCategory... categories) {
        return Accept(Arrays.stream(categories)
                .map(EventFilters::has)
                .reduce(Predicate::or)
                .orElse(it -> true));
    }

    public static EventFilter Accept(Predicate<Event> predicate) {
        return next -> event -> {
            if (predicate.test(event)) {
                next.record(event);
            }
        };
    }

    public static <T> Predicate<Event> is(Class<T> type) {
        return e -> type.isInstance(e instanceof MetadataEvent m ? m.event() : e);
    }

    public static Predicate<Event> has(EventCategory category) {
        return e -> category.equals(e.category());
    }

    public static EventFilter Reject(Predicate<Event> predicate) {
        return next -> event -> {
            if (predicate.test(event)) {
                return;
            }

            next.record(event);
        };
    }

    public static EventFilter noop() {
        return next -> next;
    }
}
