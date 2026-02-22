package com.github.toastshaman.tinytypes.aws.sns;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class NullSnsPublisher<T> implements SnsPublisher<T> {

    private final ReentrantLock lock = new ReentrantLock();

    public final LinkedList<CapturedMessage<T>> capturedMessages = new LinkedList<>();

    public record CapturedMessage<T>(T message, Map<String, MessageAttributeValue> attributes) {
        public CapturedMessage {
            Objects.requireNonNull(message);
            Objects.requireNonNull(attributes);
        }
    }

    @Override
    public void publish(T message, Map<String, MessageAttributeValue> attributes) {
        lock.lock();

        try {
            capturedMessages.add(new CapturedMessage<>(message, attributes));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void publish(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        lock.lock();

        try {
            messages.forEach(message -> capturedMessages.add(new CapturedMessage<>(message, attributes)));
        } finally {
            lock.unlock();
        }
    }
}
