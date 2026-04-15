package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.function.Function;
import tools.jackson.databind.json.JsonMapper;

public interface MessageSerializer<T> extends Function<T, String> {

    static <T> MessageSerializer<T> json(JsonMapper mapper) {
        return value -> {
            try {
                return mapper.writeValueAsString(value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize message to JSON", e);
            }
        };
    }
}
