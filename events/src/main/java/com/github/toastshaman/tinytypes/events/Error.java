package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.ERROR;

import java.util.Objects;

public record Error(String message, Throwable cause) implements Event {

    public Error {
        Objects.requireNonNull(message);
    }

    @Override
    public EventCategory category() {
        return ERROR;
    }

    public static com.github.toastshaman.tinytypes.events.Error from(String message) {
        return new com.github.toastshaman.tinytypes.events.Error(message, null);
    }

    public static com.github.toastshaman.tinytypes.events.Error from(String message, Throwable throwable) {
        return new com.github.toastshaman.tinytypes.events.Error(message, throwable);
    }
}
