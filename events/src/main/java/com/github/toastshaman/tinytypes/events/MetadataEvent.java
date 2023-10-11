package com.github.toastshaman.tinytypes.events;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import java.util.Map;
import java.util.Objects;

public record MetadataEvent(Event event, Map<String, Object> metadata) implements Event {

    public MetadataEvent(Event event, Map<String, Object> metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        this.event = Objects.requireNonNull(event, "event must not be null");
        this.metadata = Map.copyOf(metadata);
    }

    public MetadataEvent plus(String key, Object value) {
        return new MetadataEvent(event, HashMap.ofAll(metadata).put(key, value).toJavaMap());
    }

    public MetadataEvent plus(Tuple2<String, Object> value) {
        return addMetadata(value._1, value._2);
    }

    public MetadataEvent plus(Map<String, Object> value) {
        var first = HashMap.ofAll(metadata);
        var second = HashMap.ofAll(value);
        return new MetadataEvent(event, first.merge(second).toJavaMap());
    }

    public MetadataEvent withEvent(Event event) {
        return new MetadataEvent(event, Map.copyOf(metadata));
    }

    public MetadataEvent withMetadata(Map<String, Object> metadata) {
        return new MetadataEvent(event, Map.copyOf(metadata));
    }
}
