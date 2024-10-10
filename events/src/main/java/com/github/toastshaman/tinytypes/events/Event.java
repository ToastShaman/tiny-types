package com.github.toastshaman.tinytypes.events;

import java.util.Map;

public interface Event {

    default EventCategory category() {
        return EventCategory.INFO;
    }

    default MetadataEvent addMetadata(String key, Object value) {
        return this instanceof MetadataEvent m ? m.plus(key, value) : new MetadataEvent(this, Map.of(key, value));
    }
}
