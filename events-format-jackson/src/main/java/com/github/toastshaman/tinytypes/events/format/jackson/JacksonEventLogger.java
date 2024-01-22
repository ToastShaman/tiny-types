package com.github.toastshaman.tinytypes.events.format.jackson;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import java.util.Objects;
import java.util.function.Consumer;

public final class JacksonEventLogger implements Events, Consumer<String> {

    private final Consumer<String> consumer;

    private final ObjectWriter objectWriter;

    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .deactivateDefaultTyping()
            .serializationInclusion(NON_NULL)
            .enable(FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(FAIL_ON_NUMBERS_FOR_ENUMS)
            .enable(USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(USE_BIG_INTEGER_FOR_INTS)
            .enable(WRITE_BIGDECIMAL_AS_PLAIN)
            .disable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(FAIL_ON_IGNORED_PROPERTIES)
            .disable(ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .addModule(new EventsModule())
            .build();

    public JacksonEventLogger() {
        this(jsonMapper);
    }

    public JacksonEventLogger(Consumer<String> consumer) {
        this(jsonMapper, consumer);
    }

    public JacksonEventLogger(ObjectMapper mapper) {
        this(mapper, System.out::println);
    }

    public JacksonEventLogger(ObjectMapper mapper, Consumer<String> consumer) {
        this.consumer = Objects.requireNonNull(consumer);
        this.objectWriter = Objects.requireNonNull(mapper).writer();
    }

    @Override
    public void record(Event event) {
        try {
            accept(objectWriter.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Failed to write event: %s".formatted(e.getMessage()), e);
        }
    }

    @Override
    public void accept(String text) {
        consumer.accept(text);
    }
}
