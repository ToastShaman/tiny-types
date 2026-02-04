package com.github.toastshaman.tinytypes.aws.sns;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.Message;

public record SnsHeader<T>(
        String name,
        Function<T, software.amazon.awssdk.services.sns.model.MessageAttributeValue> encode,
        Function<software.amazon.awssdk.services.sqs.model.MessageAttributeValue, T> decode) {

    public SnsHeader {
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

    public static SnsHeader<String> text(String name) {
        return new SnsHeader<>(
                name,
                value -> MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value)
                        .build(),
                software.amazon.awssdk.services.sqs.model.MessageAttributeValue::stringValue);
    }
}
