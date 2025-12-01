package com.github.toastshaman.tinytypes.aws.sns;

import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.List;
import java.util.Map;

public interface SnsPublisher<T> {

    void publish(T message, Map.Entry<String, MessageAttributeValue>... attributes);

    void publish(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes);
}
