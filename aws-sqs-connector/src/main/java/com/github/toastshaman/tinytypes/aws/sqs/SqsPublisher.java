package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public interface SqsPublisher<T> {

    void publish(T message, Map.Entry<String, MessageAttributeValue>... attributes);

    void publish(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes);
}
