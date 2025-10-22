package com.github.toastshaman.tinytypes.aws.sqs;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.Message;

public final class RetryingSqsMessageHandler implements SqsMessageHandler {

    private final RetryPolicy<Void> retryPolicy;

    private final SqsMessageHandler next;

    public RetryingSqsMessageHandler(RetryPolicy<Void> retryPolicy, SqsMessageHandler next) {
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
        this.next = Objects.requireNonNull(next, "next message handler must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        Failsafe.with(retryPolicy).run(() -> next.handle(messages));
    }
}
