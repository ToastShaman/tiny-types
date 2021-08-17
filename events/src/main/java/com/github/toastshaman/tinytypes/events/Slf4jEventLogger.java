package com.github.toastshaman.tinytypes.events;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class Slf4jEventLogger implements Events {

    private static final Logger log = LoggerFactory.getLogger("EVENTS");

    private final BiConsumer<Marker, String> writer;

    private final Function<Event, JSONObject> mapper;

    public Slf4jEventLogger(Function<Event, JSONObject> mapper) {
        this(log::info, mapper);
    }

    public Slf4jEventLogger(BiConsumer<Marker, String> writer, Function<Event, JSONObject> mapper) {
        this.writer = Objects.requireNonNull(writer);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public void record(Event event) {
        writer.accept(
                MarkerFactory.getMarker(event.category().unwrap()),
                mapper.apply(event).toString());
    }
}
