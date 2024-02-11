package com.github.toastshaman.tinytypes.events;

import java.util.function.Function;

public interface EventFilter {

    Events filter(Events events);

    default EventFilter then(EventFilter next) {
        return events -> filter(next.filter(events));
    }

    default Events then(Events next) {
        return event -> filter(next).record(event);
    }

    static EventFilter of(Function<Events, Events> fn) {
        return fn::apply;
    }
}
