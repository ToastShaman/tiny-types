package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.fp.Iso;
import java.time.Instant;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public record SqsHeader<T>(String name, Iso<MessageAttributeValue, T> iso) {

    public SqsHeader {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(iso, "iso must not be null");
    }

    public T get(Message message) {
        return iso.get(message.messageAttributes().get(name));
    }

    public MessageAttributeValue reverseGet(T value) {
        return iso.reverseGet(value);
    }

    public static SqsHeader<String> text(String name) {
        return new SqsHeader<>(name, Iso.of(MessageAttributeValue::stringValue, value -> MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(value)
                .build()));
    }

    public static SqsHeader<Instant> timestamp(String name) {
        return new SqsHeader<>(
                name, Iso.of(value -> Instant.parse(value.stringValue()), value -> MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value.toString())
                        .build()));
    }
}
