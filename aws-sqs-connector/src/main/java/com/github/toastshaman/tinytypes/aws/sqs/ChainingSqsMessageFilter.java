package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.Message;

public record ChainingSqsMessageFilter<T>(SqsMessageHandler<T> handler) implements SqsMessagesHandler {

    public ChainingSqsMessageFilter {
        Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        for (Message message : messages) {
            handler.handle(message);
        }
    }
}
