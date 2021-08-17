package com.github.toastshaman.tinytypes.events;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public final class EventCategory extends NonBlankStringValue {

    public static final EventCategory INFO = EventCategory.of("INFO");
    public static final EventCategory WARN = EventCategory.of("WARN");
    public static final EventCategory ERROR = EventCategory.of("ERROR");
    public static final EventCategory AUDIT = EventCategory.of("AUDIT");
    public static final EventCategory METRIC = EventCategory.of("METRIC");

    public EventCategory(String value) {
        super(value);
    }

    public static EventCategory of(String value) {
        return new EventCategory(value);
    }
}
