package com.github.toastshaman.tinytypes.aws.sqs;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.time.Duration;
import java.util.Objects;

@RecordBuilder
public record ScheduledSqsMessageListenerOptions(Duration delay, Duration shutdownTimeout) {

    public ScheduledSqsMessageListenerOptions {
        Objects.requireNonNull(delay, "delay must not be null");
        Objects.requireNonNull(shutdownTimeout, "shutdown timeout must not be null");
    }

    public static ScheduledSqsMessageListenerOptionsBuilder builder() {
        return ScheduledSqsMessageListenerOptionsBuilder.builder();
    }
}
