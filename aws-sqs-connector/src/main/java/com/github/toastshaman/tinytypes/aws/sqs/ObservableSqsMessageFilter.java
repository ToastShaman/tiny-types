package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class ObservableSqsMessageFilter implements SqsMessagesFilter {

    private final QueueName queueName;

    private final MeterRegistry meterRegistry;

    public ObservableSqsMessageFilter(QueueName name, MeterRegistry meterRegistry) {
        this.queueName = Objects.requireNonNull(name, "name must not be null");
        this.meterRegistry = Objects.requireNonNull(meterRegistry, "meter registry must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        var name = queueName.unwrap();

        var inFlightMessages =
                meterRegistry.gauge("sqs.messages.in_flight", Tags.of("queue", name), new AtomicInteger(0));

        return messages -> {
            var sample = Timer.start(meterRegistry);

            inFlightMessages.incrementAndGet();

            try {
                next.accept(messages);

                sample.stop(Timer.builder("sqs.message.processing")
                        .tag("queue", name)
                        .tag("status", "success")
                        .register(meterRegistry));

                meterRegistry
                        .counter("sqs.messages.processed", "queue", name, "status", "success")
                        .increment();

            } catch (Exception e) {
                sample.stop(Timer.builder("sqs.message.processing")
                        .tag("queue", name)
                        .tag("status", "failure")
                        .tag("error", e.getClass().getSimpleName())
                        .register(meterRegistry));

                meterRegistry
                        .counter(
                                "sqs.messages.processed",
                                "queue",
                                name,
                                "status",
                                "failure",
                                "error",
                                e.getClass().getSimpleName())
                        .increment();

                throw e;
            } finally {
                inFlightMessages.decrementAndGet();
            }
        };
    }
}
