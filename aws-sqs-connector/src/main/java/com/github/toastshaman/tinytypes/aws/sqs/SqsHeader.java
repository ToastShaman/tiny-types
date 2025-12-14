package com.github.toastshaman.tinytypes.aws.sqs;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public record SqsHeader<T>(
        String name, Function<MessageAttributeValue, T> get, Function<T, MessageAttributeValue> reverseGet) {

    public SqsHeader {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(get, "get must not be null");
        Objects.requireNonNull(reverseGet, "reverseGet must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    public T get(Message message) {
        return get.apply(message.messageAttributes().get(name));
    }

    public Map.Entry<String, MessageAttributeValue> reverseGet(T value) {
        return Map.entry(name, reverseGet.apply(value));
    }

    public static SqsHeader<String> text(String name) {
        return new SqsHeader<>(name, MessageAttributeValue::stringValue, value -> MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(value)
                .build());
    }

    public static SqsHeader<Instant> timestamp(String name) {
        return new SqsHeader<>(
                name, value -> Instant.parse(value.stringValue()), value -> MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value.toString())
                        .build());
    }
}
