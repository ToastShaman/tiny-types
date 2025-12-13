package com.github.toastshaman.tinytypes.aws.sns;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public interface SnsPublisher<T> {

    default void publish(T message) {
        publish(message, new Map.Entry[] {});
    }

    default void publish(List<T> messages) {
        publish(messages, new Map.Entry[] {});
    }

    void publish(T message, Map.Entry<String, MessageAttributeValue>... attributes);

    void publish(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes);
}
