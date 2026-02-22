package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import software.amazon.awssdk.services.sqs.model.Message;

public class NullMutableSqsMessageListener implements SqsMessageListener {

    public int callCount = 0;

    public final LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<>();

    private final SqsMessagesHandler handler;

    private final ReentrantLock lock = new ReentrantLock();

    public NullMutableSqsMessageListener(SqsMessagesHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void poll() {
        lock.lock();

        try {
            callCount++;

            var message = q.poll();

            if (message == null) {
                return;
            }

            handler.accept(List.of(message));
        } finally {
            lock.unlock();
        }
    }

    public NullMutableSqsMessageListener addNextMessage(Message message) {
        Objects.requireNonNull(message);

        lock.lock();
        try {
            q.add(message);
        } finally {
            lock.unlock();
        }
        return this;
    }

    public NullMutableSqsMessageListener addNextMessage(Consumer<Message.Builder> builder) {
        Objects.requireNonNull(builder);

        var messageBuilder = Message.builder();
        builder.accept(messageBuilder);

        return addNextMessage(messageBuilder.build());
    }
}
