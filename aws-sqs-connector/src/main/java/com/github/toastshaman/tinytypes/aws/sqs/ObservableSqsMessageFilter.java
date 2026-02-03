package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObservableSqsMessageFilter implements SqsMessagesFilter {

    private final QueueName queueName;

    private final MeterRegistry meterRegistry;

    private final AtomicInteger inFlightMessages;

    public ObservableSqsMessageFilter(QueueName name, MeterRegistry meterRegistry) {
        this.queueName = Objects.requireNonNull(name, "name must not be null");
        this.meterRegistry = Objects.requireNonNull(meterRegistry, "meter registry must not be null");
        this.inFlightMessages = new AtomicInteger(0);

        Gauge.builder("sqs.messages.in_flight", inFlightMessages, AtomicInteger::get)
                .tag("queue", queueName.unwrap())
                .register(meterRegistry);
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            var numberOfMessages = messages.size();
            var sample = Timer.start(meterRegistry);
            inFlightMessages.addAndGet(numberOfMessages);

            try {
                next.accept(messages);
                recordSuccess(sample, numberOfMessages);
            } catch (Exception e) {
                recordFailure(sample, e, numberOfMessages);
                throw e;
            } finally {
                inFlightMessages.addAndGet(-numberOfMessages);
            }
        };
    }

    private void recordSuccess(Timer.Sample sample, int numberOfMessages) {
        var timer = Timer.builder("sqs.message.processing")
                .tag("queue", queueName.unwrap())
                .tag("status", "success")
                .register(meterRegistry);

        sample.stop(timer);

        Counter.builder("sqs.messages.processed")
                .tag("queue", queueName.unwrap())
                .tag("status", "success")
                .register(meterRegistry)
                .increment(numberOfMessages);
    }

    private void recordFailure(Timer.Sample sample, Exception e, int numberOfMessages) {
        var timer = Timer.builder("sqs.message.processing")
                .tag("queue", queueName.unwrap())
                .tag("status", "failure")
                .tag("error", e.getClass().getSimpleName())
                .register(meterRegistry);

        sample.stop(timer);

        Counter.builder("sqs.messages.processed")
                .tag("queue", queueName.unwrap())
                .tag("status", "failure")
                .tag("error", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment(numberOfMessages);
    }
}
