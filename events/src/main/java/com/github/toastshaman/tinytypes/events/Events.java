package com.github.toastshaman.tinytypes.events;

public interface Events {

    void record(Event event);

    default Events and(Events next) {
        return event -> {
            this.record(event);
            next.record(event);
        };
    }
}
