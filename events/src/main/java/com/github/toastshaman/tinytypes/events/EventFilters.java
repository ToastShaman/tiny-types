package com.github.toastshaman.tinytypes.events;

import java.time.Clock;

public class EventFilters {

    public static EventFilter AddEventName = next -> event -> {
        var aClass = event instanceof MetadataEvent m ? m.event().getClass() : event.getClass();
        next.record(event.addMetadata("name", aClass.getSimpleName()));
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

    private EventFilters() {}
}
