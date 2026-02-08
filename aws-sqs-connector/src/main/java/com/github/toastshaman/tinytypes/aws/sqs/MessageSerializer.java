package com.github.toastshaman.tinytypes.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;

public interface MessageSerializer<T> extends Function<T, String> {

    static <T> MessageSerializer<T> json(ObjectMapper mapper) {
        return value -> {
            try {
                return mapper.writeValueAsString(value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize message to JSON", e);
            }
        };
    }
}
