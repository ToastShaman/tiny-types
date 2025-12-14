package com.github.toastshaman.tinytypes.aws.sqs;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
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

    public UnaryOperator<Message> with(T value) {
        return message -> with(message, value);
    }

    public Message with(Message message, T value) {
        var attributes = new HashMap<>(message.messageAttributes());
        attributes.put(name, reverseGet.apply(value));

        return message.toBuilder().messageAttributes(attributes).build();
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

    @SafeVarargs
    public static UnaryOperator<Message> apply(UnaryOperator<Message>... operators) {
        return message -> {
            for (var operator : operators) {
                message = operator.apply(message);
            }
            return message;
        };
    }
}
