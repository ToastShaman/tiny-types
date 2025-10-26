package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.events.Events;
import dev.failsafe.RetryPolicy;
import dev.failsafe.RetryPolicyBuilder;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public static <T> ChainingSqsMessageFilter<T> ChainingSqsMessageFilter(SqsMessageHandler<T> handler) {
        return new ChainingSqsMessageFilter<>(handler);
    }
}
