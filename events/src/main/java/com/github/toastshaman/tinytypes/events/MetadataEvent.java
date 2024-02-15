package com.github.toastshaman.tinytypes.events;

import static java.util.Objects.requireNonNull;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import java.util.Map;
import java.util.Optional;

public record MetadataEvent(Event event, Map<String, Object> metadata) implements Event {

    public MetadataEvent(Event event, Map<String, Object> metadata) {
        this.event = requireNonNull(event, "event must not be null");
        this.metadata = Map.copyOf(requireNonNull(metadata, "metadata must not be null"));
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

    @SuppressWarnings("unchecked")
    public <T> Optional<T> maybe(String key) {
        return Optional.ofNullable((T) metadata.get(key));
    }
}
