package com.github.toastshaman.tinytypes.aws.sqs;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class RetryingSqsMessageFilter implements SqsMessagesFilter {

    private final RetryPolicy<Void> retryPolicy;

    public RetryingSqsMessageFilter(RetryPolicy<Void> retryPolicy) {
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler handler) {
        return messages -> Failsafe.with(retryPolicy).run(() -> handler.handle(messages));
    }
}
