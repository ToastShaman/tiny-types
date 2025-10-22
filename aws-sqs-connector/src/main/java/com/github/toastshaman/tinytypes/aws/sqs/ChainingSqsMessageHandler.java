package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

public final class ChainingSqsMessageHandler<T> implements SqsMessageHandler {

    private final Function<Message, T> handlers;

    public ChainingSqsMessageHandler(Function<Message, T> handlers) {
        this.handlers = Objects.requireNonNull(handlers);
    }

    @Override
    public void handle(List<Message> messages) {
        messages.forEach(handlers::apply);
    }
}
