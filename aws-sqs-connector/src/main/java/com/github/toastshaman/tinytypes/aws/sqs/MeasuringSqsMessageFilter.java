package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.MeasuringSqsMessageFilterSqsMessageProcessingTimeBuilder.SqsMessageProcessingTime;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

public record MeasuringSqsMessageFilter(Supplier<Instant> clock, Events events) implements SqsMessageFilter {

    public MeasuringSqsMessageFilter {
        Objects.requireNonNull(clock, "clock must not be null");
        Objects.requireNonNull(events, "events must not be null");
    }

    @RecordBuilder
    @RecordBuilder.Options(addClassRetainedGenerated = true)
    public record SqsMessageProcessingTime(Duration elapsedTime) implements Event {
        public SqsMessageProcessingTime {
            Objects.requireNonNull(elapsedTime, "elapsedTime must not be null");
        }
    }

    @Override
    public SqsMessageHandler filter(SqsMessageHandler handler) {
        return messages -> {
            var startTime = clock.get();
            try {
                handler.handle(messages);
            } finally {
                var endTime = clock.get();
                events.record(SqsMessageProcessingTime(Duration.between(startTime, endTime)));
            }
        };
    }
}
