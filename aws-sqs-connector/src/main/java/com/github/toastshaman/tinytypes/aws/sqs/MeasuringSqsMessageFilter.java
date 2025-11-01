package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.MeasuringSqsMessageFilterSqsMessageProcessingTimeBuilder.SqsMessageProcessingTime;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.Events;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class MeasuringSqsMessageFilter implements SqsMessagesFilter {

    private final Supplier<Instant> clock;

    private final Events events;

    public MeasuringSqsMessageFilter(Supplier<Instant> clock, Events events) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        this.events = Objects.requireNonNull(events, "events must not be null");
    }

    @RecordBuilder
    @RecordBuilder.Options(addClassRetainedGenerated = true)
    public record SqsMessageProcessingTime(Duration elapsedTime) implements Event {
        public SqsMessageProcessingTime {
            Objects.requireNonNull(elapsedTime, "elapsedTime must not be null");
        }
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            var startTime = clock.get();
            try {
                next.handle(messages);
            } finally {
                var endTime = clock.get();
                events.record(SqsMessageProcessingTime(Duration.between(startTime, endTime)));
            }
        };
    }
}
