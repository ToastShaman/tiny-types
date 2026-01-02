package com.github.toastshaman.tinytypes.aws.sqs;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public record SqsHeader<T>(
        String name, Function<T, MessageAttributeValue> encode, Function<MessageAttributeValue, T> decode) {

    public SqsHeader {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(encode, "encode must not be null");
        Objects.requireNonNull(decode, "decode must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    public T from(Message message) {
        var messageAttributes = message.messageAttributes();
        var value = messageAttributes.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Message is missing expected attribute: %s".formatted(name));
        }
        return decode.apply(value);
    }

    public Map.Entry<String, MessageAttributeValue> with(T value) {
        return Map.entry(name, encode.apply(value));
    }

    public static SqsHeader<String> text(String name) {
        return new SqsHeader<>(
                name,
                value -> MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value)
                        .build(),
                MessageAttributeValue::stringValue);
    }

    public static SqsHeader<Instant> timestamp(String name) {
        return new SqsHeader<>(
                name,
                value -> MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value.toString())
                        .build(),
                value -> Instant.parse(value.stringValue()));
    }
}
