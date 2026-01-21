package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.events.Events;
import dev.failsafe.RetryPolicy;
import dev.failsafe.RetryPolicyBuilder;
import java.time.Clock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

public final class SqsMessageFilters {

    private SqsMessageFilters() {}

    public static RetryingSqsMessageFilter RetryingSqsMessageFilter(Consumer<RetryPolicyBuilder<Void>> configurer) {
        var builder = RetryPolicy.<Void>builder();
        configurer.accept(builder);
        return new RetryingSqsMessageFilter(builder.build());
    }

    public static MeasuringSqsMessageFilter MeasuringSqsMessageFilter(Events events) {
        return new MeasuringSqsMessageFilter(Clock.systemUTC(), events);
    }

    public static MeasuringSqsMessageFilter MeasuringSqsMessageFilter(Clock clock, Events events) {
        return new MeasuringSqsMessageFilter(clock, events);
    }

    public static <R> DelegatingSqsMessageHandler<R> DelegatingSqsMessageHandler(
            SqsMessageHandler<Message, R> handler) {
        return new DelegatingSqsMessageHandler<>(handler);
    }

    public static ForwardToDeadLetterQueueOnExceptionFilter ForwardToDeadLetterQueueOnExceptionFilter(
            DeadLetterQueueUrl queueUrl, SqsClient sqs) {
        return new ForwardToDeadLetterQueueOnExceptionFilter(queueUrl, sqs);
    }

    public static ForwardToDeadLetterQueueOnExceptionFilter ForwardToDeadLetterQueueOnExceptionFilter(
            DeadLetterQueueUrl queueUrl, SqsClient sqs, Predicate<Exception> filter) {
        return new ForwardToDeadLetterQueueOnExceptionFilter(queueUrl, sqs, filter);
    }
}
