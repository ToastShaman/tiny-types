package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public class NullSqsSender<T> implements SqsSender<T> {

    public record CapturedMessage<T>(T message, Map<String, MessageAttributeValue> attributes) {}

    public final LinkedList<CapturedMessage<T>> capturedMessages = new LinkedList<>();

    @Override
    public void send(T message, Map<String, MessageAttributeValue> attributes) {
        capturedMessages.add(new CapturedMessage<>(message, attributes));
    }

    @Override
    public void send(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        messages.forEach(message -> capturedMessages.add(new CapturedMessage<>(message, attributes)));
    }
}
