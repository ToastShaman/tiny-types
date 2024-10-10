package com.github.toastshaman.tinytypes.events;

import java.time.Clock;
import java.util.Arrays;
import java.util.function.Predicate;
import org.slf4j.MDC;

public final class EventFilters {

    private EventFilters() {}

    public static EventFilter AddEventName = next -> event -> {
        var aClass = event instanceof MetadataEvent m ? m.event().getClass() : event.getClass();
        var eventWithName = event.addMetadata("name", aClass.getSimpleName());
        next.record(eventWithName);
    };

    public static EventFilter AddServiceName(String name) {
        return next -> event -> next.record(event.addMetadata("service", name));
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
        return e -> category == e.category();
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
