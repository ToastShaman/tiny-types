package com.github.toastshaman.tinytypes.events.format.jackson;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import java.util.Objects;
import java.util.function.Consumer;

public final class JacksonEventLogger implements Events, Consumer<String> {

    private final Consumer<String> writer;

    private final ObjectWriter json;

    public JacksonEventLogger() {
        this(JsonMapper.builder()
                .addModule(new EventsModule())
                .enable(WRITE_BIGDECIMAL_AS_PLAIN)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .serializationInclusion(NON_NULL)
                .build());
    }

    public JacksonEventLogger(Consumer<String> writer) {
        this(
                writer,
                JsonMapper.builder()
                        .addModule(new EventsModule())
                        .enable(WRITE_BIGDECIMAL_AS_PLAIN)
                        .disable(WRITE_DATES_AS_TIMESTAMPS)
                        .serializationInclusion(NON_NULL)
                        .build());
    }

    public JacksonEventLogger(ObjectMapper mapper) {
        this(System.out::println, mapper);
    }

    public JacksonEventLogger(Consumer<String> writer, ObjectMapper mapper) {
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
        this.json = Objects.requireNonNull(mapper, "mapper must not be null").writer();
    }

    @Override
    public void record(Event event) {
        try {
            accept(json.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(String text) {
        writer.accept(text);
    }
}
