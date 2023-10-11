package com.github.toastshaman.tinytypes.events;

import java.util.Map;
import java.util.Objects;

public interface Event {

    default MetadataEvent addMetadata(String key, Object value) {
        if (this instanceof MetadataEvent m) {
            return m.plus(key, value);
        }
        return new MetadataEvent(this, Map.of(key, value));
    }

    record Error(String message, Throwable cause, EventCategory category) implements Event {

        private static final EventCategory error = EventCategory.of("error");

        public Error(String message) {
            this(message, null, error);
        }

        public Error(String message, Throwable throwable) {
            this(message, throwable, error);
        }

        public Error {
            Objects.requireNonNull(message);
            Objects.requireNonNull(category);
        }
    }
}
