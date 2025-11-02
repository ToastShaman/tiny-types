package com.github.toastshaman.tinytypes.aws.sqs;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
@RecordBuilder.Options(addClassRetainedGenerated = true)
public record PollingSqsMessageListenerOptions(int maxNumberOfMessages, int waitTimeSeconds) {
    public PollingSqsMessageListenerOptions {
        if (maxNumberOfMessages < 1 || maxNumberOfMessages > 10) {
            throw new IllegalArgumentException("maxNumberOfMessages must be between 1 and 10");
        }

        if (waitTimeSeconds < 0) {
            throw new IllegalArgumentException("waitTimeSeconds must be greater than 0");
        }
    }
}
