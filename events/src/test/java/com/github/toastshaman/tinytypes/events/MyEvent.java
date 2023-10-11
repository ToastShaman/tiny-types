package com.github.toastshaman.tinytypes.events;

import java.util.UUID;

record MyEvent(UUID id) implements Event {
    public static MyEvent random() {
        return new MyEvent(UUID.randomUUID());
    }
}
