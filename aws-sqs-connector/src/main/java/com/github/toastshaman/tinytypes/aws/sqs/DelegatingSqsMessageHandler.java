package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

public final class DelegatingSqsMessageHandler<T> implements SqsMessagesHandler {

    private final Function<Message, T> handler;

    public DelegatingSqsMessageHandler(Function<Message, T> handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void accept(List<Message> messages) {
        for (Message message : messages) {
            handler.apply(message);
        }
    }

    public static DelegatingSqsMessageHandler<Message> from(Consumer<Message> handler) {
        return new DelegatingSqsMessageHandler<>(message -> {
            handler.accept(message);
            return message;
        });
    }

    public static <T> DelegatingSqsMessageHandler<T> of(Function<Message, T> handler) {
        return new DelegatingSqsMessageHandler<>(handler);
    }
}
