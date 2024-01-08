package com.github.toastshaman.tinytypes.format.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.vavr.control.Try;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class JsonPaths {

    private JsonPaths() {}

    public static JsonPathContext parse(String json) {
        return customise(Json.standard().mapper()).apply(json);
    }

    public static Function<String, JsonPathContext> customise(ObjectMapper mapper) {
        var cfg = Configuration.builder()
                .jsonProvider(new JacksonJsonProvider(mapper))
                .mappingProvider(new JacksonMappingProvider(mapper))
                .build();

        var parseContext = JsonPath.using(cfg);

        return json -> new JsonPathContext(() -> parseContext.parse(json));
    }

    public static class JsonPathContext {

        private final Try<DocumentContext> context;

        public JsonPathContext(Supplier<DocumentContext> context) {
            this.context = Try.ofSupplier(context);
        }

        public Try<String> readString(String path) {
            return read(path, String.class);
        }

        public Try<Long> readLong(String path) {
            return read(path, Long.class);
        }

        public Try<Double> readDouble(String path) {
            return read(path, Double.class);
        }

        public <T> Try<T> read(String path, Class<T> type) {
            return context.map(it -> it.read(path, type));
        }

        public <T> Try<T> read(String path) {
            return context.map(it -> it.read(path));
        }
    }
}
