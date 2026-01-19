package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ObservableSqsMessageFilterTest {

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private final QueueName queueName = QueueName.of("test-queue");

    @Test
    void should_record_successful_message_processing() {
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var handlerCalled = new AtomicBoolean(false);

        SqsMessagesHandler handler = messages -> handlerCalled.set(true);
        var decoratedHandler = filter.filter(handler);

        decoratedHandler.handle(List.of());

        assertThat(handlerCalled.get()).isTrue();
        assertThat(meterRegistry
                        .counter("sqs.messages.processed", "queue", "test-queue", "status", "success")
                        .count())
                .isEqualTo(1.0);
    }

    @Test
    void should_record_failed_message_processing() {
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);

        SqsMessagesHandler handler = messages -> {
            throw new IllegalArgumentException("Processing failed");
        };
        var decoratedHandler = filter.filter(handler);

        assertThatThrownBy(() -> decoratedHandler.handle(List.of())).isInstanceOf(IllegalArgumentException.class);

        assertThat(meterRegistry
                        .counter(
                                "sqs.messages.processed",
                                "queue",
                                "test-queue",
                                "status",
                                "failure",
                                "error",
                                "IllegalArgumentException")
                        .count())
                .isEqualTo(1.0);
    }

    @Test
    void should_track_in_flight_messages() {
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var inFlightDuringProcessing = new AtomicReference<Double>();

        SqsMessagesHandler handler = messages -> {
            var gauge = meterRegistry
                    .find("sqs.messages.in_flight")
                    .tag("queue", "test-queue")
                    .gauge();
            inFlightDuringProcessing.set(gauge.value());
        };
        var decoratedHandler = filter.filter(handler);

        decoratedHandler.handle(List.of());

        assertThat(inFlightDuringProcessing.get()).isEqualTo(1.0);
        assertThat(meterRegistry
                        .find("sqs.messages.in_flight")
                        .tag("queue", "test-queue")
                        .gauge()
                        .value())
                .isEqualTo(0.0);
    }

    @Test
    void should_decrement_in_flight_counter_on_failure() {
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);

        SqsMessagesHandler handler = messages -> {
            throw new RuntimeException("Failed");
        };
        var decoratedHandler = filter.filter(handler);

        assertThatThrownBy(() -> decoratedHandler.handle(List.of())).isInstanceOf(RuntimeException.class);

        assertThat(meterRegistry
                        .find("sqs.messages.in_flight")
                        .tag("queue", "test-queue")
                        .gauge()
                        .value())
                .isEqualTo(0.0);
    }

    @Test
    void should_record_processing_timer_with_success_status() {
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var decoratedHandler = filter.filter(messages -> {});

        decoratedHandler.handle(List.of());

        var timer = meterRegistry
                .find("sqs.message.processing")
                .tag("queue", "test-queue")
                .tag("status", "success")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    void should_record_processing_timer_with_failure_status() {
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var decoratedHandler = filter.filter(messages -> {
            throw new IllegalStateException("Error");
        });

        assertThatThrownBy(() -> decoratedHandler.handle(List.of())).isInstanceOf(IllegalStateException.class);

        var timer = meterRegistry
                .find("sqs.message.processing")
                .tag("queue", "test-queue")
                .tag("status", "failure")
                .tag("error", "IllegalStateException")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }
}
