package com.github.toastshaman.tinytypes.aws.sqs;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import java.util.Objects;

public record RetryingSqsMessageFilter(RetryPolicy<Void> retryPolicy) implements SqsMessagesFilter {

    public RetryingSqsMessageFilter {
        Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler handler) {
        return messages -> Failsafe.with(retryPolicy).run(() -> handler.handle(messages));
    }
}
