package com.github.toastshaman.tinytypes.events;

import java.util.Objects;

public record EventCategory(String value) {

    public static EventCategory INFO = EventCategory.of("info");

    public static EventCategory ERROR = EventCategory.of("error");

    public static EventCategory WARN = EventCategory.of("warn");

    public EventCategory {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }

    public static EventCategory of(String value) {
        return new EventCategory(value);
    }
}
