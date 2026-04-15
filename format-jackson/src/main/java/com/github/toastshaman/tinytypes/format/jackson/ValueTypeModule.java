package com.github.toastshaman.tinytypes.format.jackson;

import com.github.toastshaman.tinytypes.values.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Function;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

public class ValueTypeModule extends SimpleModule {

    public <T extends UUIDValue> ValueTypeModule uuid(Class<T> clazz, Function<UUID, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(UUID.fromString(p.getString()));
            }
        });

        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });

        return this;
    }

    public <T extends StringValue> ValueTypeModule text(Class<T> clazz, Function<String, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(p.getString());
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends InstantValue> ValueTypeModule instant(Class<T> clazz, Function<Instant, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(Instant.parse(p.getString()));
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends OffsetDateTimeValue> ValueTypeModule offset(Class<T> clazz, Function<OffsetDateTime, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(OffsetDateTime.parse(p.getString()));
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends OffsetDateTimeValue> ValueTypeModule offsetWithFormatter(
            Class<T> clazz, Function<OffsetDateTime, T> fn, DateTimeFormatter formatter) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(OffsetDateTime.from(formatter.parse(p.getString())));
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, formatter.format(value.unwrap()));
            }
        });
        return this;
    }

    public <T extends LongValue> ValueTypeModule number(Class<T> clazz, Function<Long, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(new BigDecimal(p.getString()).longValueExact());
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends BigIntegerValue> ValueTypeModule bigIntegerValue(Class<T> clazz, Function<BigInteger, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(new BigInteger(p.getString()));
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends IntegerValue> ValueTypeModule integerValue(Class<T> clazz, Function<Integer, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(new BigDecimal(p.getString()).intValueExact());
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }

    public <T extends BooleanValue> ValueTypeModule booleanValue(Class<T> clazz, Function<Boolean, T> fn) {
        addDeserializer(clazz, new ValueDeserializer<>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) {
                return fn.apply(Boolean.valueOf(p.getString()));
            }
        });
        addSerializer(clazz, new ValueSerializer<>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                ctxt.writeValue(gen, value.unwrap());
            }
        });
        return this;
    }
}
