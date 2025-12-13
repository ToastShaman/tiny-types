package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public interface SqsPublisher<T> {

    default void publish(T message) {
        publish(message, new Map.Entry[] {});
    }

    default void publish(List<T> messages) {
        publish(messages, new Map.Entry[] {});
    }

    void publish(T message, Map.Entry<String, MessageAttributeValue>... attributes);

    void publish(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes);
}
