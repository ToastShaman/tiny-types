package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.toastshaman.tinytypes.values.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Function;

public class ValueTypeModule extends SimpleModule {

    public <T extends UUIDValue> ValueTypeModule uuid(Class<T> clazz, Function<UUID, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(UUID.fromString(p.getText()));
            }
        });

        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends StringValue> ValueTypeModule text(Class<T> clazz, Function<String, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(p.getText());
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends InstantValue> ValueTypeModule instant(Class<T> clazz, Function<Instant, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(Instant.parse(p.getText()));
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends OffsetDateTimeValue> ValueTypeModule offset(Class<T> clazz, Function<OffsetDateTime, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(OffsetDateTime.parse(p.getText()));
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends OffsetDateTimeValue> ValueTypeModule offsetWithFormatter(
            Class<T> clazz, Function<OffsetDateTime, T> fn, DateTimeFormatter formatter) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(OffsetDateTime.from(formatter.parse(p.getText())));
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, formatter.format(value.unwrap()));
            }
        });
        return this;
    }

    public <T extends LongValue> ValueTypeModule number(Class<T> clazz, Function<Long, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(new BigDecimal(p.getText()).longValueExact());
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends BigIntegerValue> ValueTypeModule bigIntegerValue(Class<T> clazz, Function<BigInteger, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(new BigInteger(p.getText()));
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends IntegerValue> ValueTypeModule integerValue(Class<T> clazz, Function<Integer, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(new BigDecimal(p.getText()).intValueExact());
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends BooleanValue> ValueTypeModule booleanValue(Class<T> clazz, Function<Boolean, T> fn) {
        addDeserializer(clazz, new JsonDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return fn.apply(Boolean.valueOf(p.getText()));
            }
        });
        addSerializer(clazz, new JsonSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.getCodec().writeValue(gen, value.unwrap());
            }
        });
        return this;
    }
}
