package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.events.Events;
import dev.failsafe.RetryPolicy;
import dev.failsafe.RetryPolicyBuilder;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.services.sqs.model.Message;

public final class SqsMessageFilters {

    private SqsMessageFilters() {}

    public static RetryingSqsMessageFilter RetryingSqsMessageFilter(Consumer<RetryPolicyBuilder<Void>> configurer) {
        var builder = RetryPolicy.<Void>builder();
        configurer.accept(builder);
        return new RetryingSqsMessageFilter(builder.build());
    }

    public static MeasuringSqsMessageFilter MeasuringSqsMessageFilter(Events events) {
        return new MeasuringSqsMessageFilter(Instant::now, events);
    }

    public static MeasuringSqsMessageFilter MeasuringSqsMessageFilter(Supplier<Instant> clock, Events events) {
        return new MeasuringSqsMessageFilter(clock, events);
    }

    public static <T> ChainingSqsMessageFilter<T> ChainingSqsMessageFilter(Function<Message, T> handlers) {
        return new ChainingSqsMessageFilter<>(handlers);
    }
}
