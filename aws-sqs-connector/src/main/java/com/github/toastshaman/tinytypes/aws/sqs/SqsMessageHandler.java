package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

public interface SqsMessageHandler<T> {

    T handle(Message messages);

    default <R> SqsMessageHandler<R> andThen(Function<T, R> next) {
        return message -> next.apply(handle(message));
    }

    static <T> SqsMessageHandler<T> fromFunction(Function<Message, T> function) {
        return function::apply;
    }

    static <T> SqsMessageHandler<T> fromConsumer(Consumer<Message> function) {
        return message -> {
            function.accept(message);
            return null;
        };
    }
}
