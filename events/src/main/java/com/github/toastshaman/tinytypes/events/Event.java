package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.INFO;

public interface Event {

    default EventCategory category() {
        return INFO;
    }
}
