package com.github.toastshaman.tinytypes.aws.sns;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public interface SnsPublisher<T> {

    void publish(T message, Map<String, MessageAttributeValue> attributes);

    default void publish(T message) {
        publish(message, Map.of());
    }

    void publish(List<T> messages, Map<String, MessageAttributeValue> attributes);

    default void publish(List<T> messages) {
        publish(messages, Map.of());
    }
}
