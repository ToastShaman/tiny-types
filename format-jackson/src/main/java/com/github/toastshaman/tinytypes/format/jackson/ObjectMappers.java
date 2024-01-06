package com.github.toastshaman.tinytypes.format.jackson;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
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
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.toastshaman.tinytypes.format.jackson.JsonPaths.JsonPathContext;
import io.vavr.Function0;
import io.vavr.Lazy;
import io.vavr.control.Try;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ObjectMappers {

    public static final Function0<Builder> jsonMapperF = () -> JsonMapper.builder()
            .enable(FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(FAIL_ON_NUMBERS_FOR_ENUMS)
            .enable(USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(USE_BIG_INTEGER_FOR_INTS)
            .enable(WRITE_BIGDECIMAL_AS_PLAIN)
            .disable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(FAIL_ON_IGNORED_PROPERTIES)
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .addModule(new JsonOrgModule());

    public static final ObjectMapper mapper = jsonMapperF.get().build();

    private ObjectMappers() {}

    public Lazy<ObjectMapper> customise(UnaryOperator<Builder> f) {
        return Lazy.of(() -> f.apply(jsonMapperF.get()).build());
    }

    public static JsonPathContext parse(String json) {
        return JsonPaths.parse(json);
    }

    public static Try<JSONObject> readAsJSONObject(String json) {
        return read(json, JSONObject.class);
    }

    public static Try<JSONArray> readAsJSONArray(String json) {
        return read(json, JSONArray.class);
    }

    public static <T> Try<T> read(String json, Class<T> clazz) {
        return Try.of(() -> mapper.readValue(json, clazz));
    }

    public static <T> Try<T> read(String json, TypeReference<T> clazz) {
        return Try.of(() -> mapper.readValue(json, clazz));
    }

    public static <T> Try<List<T>> readList(String json) {
        return Try.of(() -> mapper.readValue(json, new TypeReference<>() {}));
    }

    public static <T> Try<String> write(T object) {
        return write(() -> object);
    }

    public static <T> Try<String> write(Supplier<T> f) {
        return Try.of(() -> mapper.writeValueAsString(f.get()));
    }

    public static <T> Try<JSONObject> convert(T object) {
        return convert(() -> object);
    }

    public static <T> Try<JSONObject> convert(Supplier<T> f) {
        return Try.of(() -> new JSONObject(mapper.writeValueAsString(f.get())));
    }
}
