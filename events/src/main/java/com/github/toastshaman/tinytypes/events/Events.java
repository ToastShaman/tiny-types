package com.github.toastshaman.tinytypes.events;

public interface Events {

    void record(Event event);

    default Events appendNext(Events next) {
        return event -> {
            Events.this.record(event);
            next.record(event);
        };
    }
}
