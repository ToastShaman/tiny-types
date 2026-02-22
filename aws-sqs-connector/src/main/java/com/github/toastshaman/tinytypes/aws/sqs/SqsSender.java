package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public interface SqsSender<T> {

    void send(T message, Map<String, MessageAttributeValue> attributes);

    void send(List<T> messages, Map<String, MessageAttributeValue> attributes);

    default void send(T message) {
        send(message, Map.of());
    }

    default void send(List<T> messages) {
        send(messages, Map.of());
    }
}
