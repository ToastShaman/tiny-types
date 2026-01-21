package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class DelegatingSqsMessageHandler<T> implements SqsMessagesHandler {

    private final SqsMessageHandler<Message, T> handler;

    public DelegatingSqsMessageHandler(SqsMessageHandler<Message, T> handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        for (Message message : messages) {
            handler.handle(message);
        }
    }
}
