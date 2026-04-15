package com.github.toastshaman.tinytypes.events.format.jackson;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static tools.jackson.core.StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static tools.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;
import static tools.jackson.databind.cfg.EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import java.util.Objects;
import java.util.function.Consumer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

public final class JacksonEventLogger implements Events, Consumer<String> {

    private final Consumer<String> consumer;

    private final ObjectWriter objectWriter;

    public static final JsonMapper jsonMapper = JsonMapper.builder()
            .deactivateDefaultTyping()
            .changeDefaultPropertyInclusion(v -> v.withValueInclusion(NON_NULL))
            .enable(FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(FAIL_ON_NUMBERS_FOR_ENUMS)
            .enable(USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(USE_BIG_INTEGER_FOR_INTS)
            .enable(WRITE_BIGDECIMAL_AS_PLAIN)
            .disable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(FAIL_ON_IGNORED_PROPERTIES)
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
