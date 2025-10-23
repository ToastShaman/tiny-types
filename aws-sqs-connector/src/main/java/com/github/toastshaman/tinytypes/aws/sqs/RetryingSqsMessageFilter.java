package com.github.toastshaman.tinytypes.aws.sqs;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import java.util.Objects;

public record RetryingSqsMessageFilter(RetryPolicy<Void> retryPolicy) implements SqsMessageFilter {

    public RetryingSqsMessageFilter {
        Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
    }

    @Override
    public SqsMessageHandler filter(SqsMessageHandler handler) {
        Objects.requireNonNull(handler, "handler must not be null");
        return messages -> Failsafe.with(retryPolicy).run(() -> handler.handle(messages));
    }
}
