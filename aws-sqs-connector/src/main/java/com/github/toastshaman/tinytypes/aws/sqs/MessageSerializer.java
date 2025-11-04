package com.github.toastshaman.tinytypes.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface MessageSerializer<T> {

    String serialize(T value);

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
