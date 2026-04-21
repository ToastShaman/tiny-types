package com.github.toastshaman.tinytypes.aws.sqs;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Objects;

@RecordBuilder
public record ParallelSqsMessageListenerOptions(int threads, ParallelSqsMessageListenerThreadType threadType) {

    public ParallelSqsMessageListenerOptions {
        if (threads < 1) {
            throw new IllegalArgumentException("threads must be greater than 0");
        }

        Objects.requireNonNull(threadType, "thread type must not be null");
    }

    public static ParallelSqsMessageListenerOptionsBuilder builder() {
        return ParallelSqsMessageListenerOptionsBuilder.builder();
    }

    public static ParallelSqsMessageListenerOptions availableCpus(ParallelSqsMessageListenerThreadType threadType) {
        return new ParallelSqsMessageListenerOptions(Runtime.getRuntime().availableProcessors(), threadType);
    }
}
