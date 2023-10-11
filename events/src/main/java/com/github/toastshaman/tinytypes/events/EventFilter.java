package com.github.toastshaman.tinytypes.events;

import java.util.function.Function;

public interface EventFilter {

    Events filter(Events events);

    default EventFilter then(EventFilter next) {
        return it -> filter(next.filter(it));
    }

    default Events then(Events next) {
        return it -> filter(next).record(it);
    }

    static EventFilter of(Function<Events, Events> fn) {
        return fn::apply;
    }
}
