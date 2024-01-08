package com.github.toastshaman.tinytypes.format.jackson;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.toastshaman.tinytypes.format.jackson.JsonPaths.JsonPathContext;
import io.vavr.Lazy;
import io.vavr.control.Try;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.json.JSONArray;
import org.json.JSONObject;

public final class Json {

    public static final Supplier<Builder> jsonMapper = () -> JsonMapper.builder()
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
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .addModule(new JsonOrgModule());

    public static final Supplier<Builder> strictJsonMapper =
            () -> jsonMapper.get().enable(FAIL_ON_UNKNOWN_PROPERTIES).enable(FAIL_ON_IGNORED_PROPERTIES);

    private static final Lazy<JsonCodec> strict =
            Lazy.of(strictJsonMapper).map(MapperBuilder::build).map(JsonCodec::from);

    private static final Lazy<JsonCodec> standard =
            Lazy.of(jsonMapper).map(MapperBuilder::build).map(JsonCodec::from);

    private Json() {}

    public static JsonCodec standard() {
        return standard.get();
    }

    public static JsonCodec strict() {
        return strict.get();
    }

    public static JsonCodec customise(ObjectMapper mapper) {
        return JsonCodec.from(mapper);
    }

    public record JsonCodec(JsonEncoder encoder, JsonDecoder decoder, ObjectMapper mapper) {
        public JsonCodec {
            Objects.requireNonNull(encoder);
            Objects.requireNonNull(decoder);
            Objects.requireNonNull(mapper);
        }

        public static JsonCodec from(ObjectMapper mapper) {
            return new JsonCodec(new JsonEncoder(mapper), new JsonDecoder(mapper), mapper);
        }
    }

    public static final class JsonEncoder {
        private final ObjectMapper mapper;

        public JsonEncoder(ObjectMapper mapper) {
            this.mapper = Objects.requireNonNull(mapper);
        }

        public <T> Try<String> write(T object) {
            return write(() -> object);
        }

        public <T> Try<String> write(Supplier<T> f) {
            return Try.of(() -> mapper.writeValueAsString(f.get()));
        }

        public <T> Try<Void> write(Writer writer, T object) {
            return write(writer, () -> object);
        }

        public <T> Try<Void> write(Writer writer, Supplier<T> object) {
            return Try.withResources(() -> writer).of(it -> {
                mapper.writeValue(it, object.get());
                return null;
            });
        }

        public <T> Try<Void> write(OutputStream outputStream, T object) {
            return write(outputStream, () -> object);
        }

        public <T> Try<Void> write(OutputStream outputStream, Supplier<T> object) {
            return Try.withResources(() -> outputStream).of(it -> {
                mapper.writeValue(it, object.get());
                return null;
            });
        }

        public <T> Try<JSONObject> convertToJSON(T object) {
            return convertToJSON(() -> object);
        }

        public <T> Try<JSONObject> convertToJSON(Supplier<T> f) {
            return Try.of(() -> new JSONObject(mapper.writeValueAsString(f.get())));
        }

        public <T> Try<Map<String, Object>> convert(T object) {
            return convert(() -> object);
        }

        public <T> Try<Map<String, Object>> convert(Supplier<T> object) {
            return Try.of(() -> mapper.convertValue(object.get(), new TypeReference<>() {}));
        }
    }

    public static final class JsonDecoder {
        private final ObjectMapper mapper;

        public JsonDecoder(ObjectMapper mapper) {
            this.mapper = Objects.requireNonNull(mapper);
        }

        public <T> Try<T> read(String json, Class<T> clazz) {
            return Try.of(() -> mapper.readValue(json, clazz));
        }

        public <T> Try<T> read(String json, TypeReference<T> clazz) {
            return Try.of(() -> mapper.readValue(json, clazz));
        }

        public <T> Try<List<T>> readList(String json) {
            return Try.of(() -> mapper.readValue(json, new TypeReference<>() {}));
        }

        public JsonPathContext parse(String json) {
            return JsonPaths.parse(json);
        }

        public Try<JSONObject> readJSONObject(String json) {
            return read(json, JSONObject.class);
        }

        public Try<JSONArray> readJSONArray(String json) {
            return read(json, JSONArray.class);
        }
    }
}
