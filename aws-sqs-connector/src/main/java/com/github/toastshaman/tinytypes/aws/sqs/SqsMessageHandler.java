package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.function.Function;

public interface SqsMessageHandler<T, R> {

    R handle(T t);

    default <V> SqsMessageHandler<T, V> andThen(SqsMessageHandler<R, V> after) {
        return t -> after.handle(handle(t));
    }

    static <T, R> SqsMessageHandler<T, R> of(Function<T, R> f) {
        return f::apply;
    }
}
