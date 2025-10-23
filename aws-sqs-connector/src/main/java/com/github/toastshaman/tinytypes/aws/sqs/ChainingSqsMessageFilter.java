package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

public record ChainingSqsMessageFilter<T>(Function<Message, T> handlers) implements SqsMessageHandler {

    public ChainingSqsMessageFilter {
        Objects.requireNonNull(handlers, "handlers must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        for (Message message : messages) {
            handlers.apply(message);
        }
    }
}
