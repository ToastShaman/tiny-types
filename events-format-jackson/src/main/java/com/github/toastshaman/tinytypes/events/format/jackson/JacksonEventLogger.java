package com.github.toastshaman.tinytypes.events.format.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import java.util.Objects;
import java.util.function.Consumer;

public final class JacksonEventLogger implements Events {

    private final Consumer<String> writer;

    private final ObjectMapper mapper;

    public JacksonEventLogger(ObjectMapper mapper) {
        this(System.out::println, mapper);
    }

    public JacksonEventLogger(Consumer<String> writer, ObjectMapper mapper) {
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    }

    @Override
    public void record(Event event) {
        try {
            writer.accept(mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
