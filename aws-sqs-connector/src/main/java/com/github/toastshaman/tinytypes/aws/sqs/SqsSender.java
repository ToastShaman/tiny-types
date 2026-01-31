package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public interface SqsSender<T> {

    void send(T message, Map.Entry<String, MessageAttributeValue>... attributes);

    void send(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes);
}
