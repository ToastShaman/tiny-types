package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ObservableSqsMessageFilterTest {

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private final QueueName queueName = QueueName.of("test-queue");

    @Test
    void should_record_successful_message_processing() {
        // given
        var handlerCalled = new AtomicBoolean(false);
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var chain = filter.filter(messages -> handlerCalled.set(true));

        // when
        chain.accept(List.of(aRadomMessage(), aRadomMessage()));

        // then
        assertThat(handlerCalled).isTrue();

        var counter = meterRegistry
                .find("sqs.messages.processed")
                .tag("queue", "test-queue")
                .tag("status", "success")
                .counter();

        assertThat(counter).isNotNull().extracting(Counter::count).isEqualTo(2.0);
    }

    @Test
    void should_record_failed_message_processing() {
        // given
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);

        var chain = filter.filter(messages -> {
            throw new IllegalArgumentException("Processing failed");
        });

        // when
        assertThatThrownBy(() -> chain.accept(List.of(aRadomMessage(), aRadomMessage())))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        var counter = meterRegistry
                .find("sqs.messages.processed")
                .tag("queue", "test-queue")
                .tag("status", "failure")
                .tag("error", "IllegalArgumentException")
                .counter();

        assertThat(counter).isNotNull().extracting(Counter::count).isEqualTo(2.0);
    }

    @Test
    void should_track_in_flight_messages() {
        // given
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var captured = new AtomicReference<Double>();

        var gauge = meterRegistry
                .find("sqs.messages.in_flight")
                .tag("queue", "test-queue")
                .gauge();

        assertThat(gauge).isNotNull();

        var chain = filter.filter(messages -> captured.set(gauge.value()));

        // when
        chain.accept(List.of(aRadomMessage(), aRadomMessage()));

        // then
        assertThat(captured).hasValue(2.0);
        assertThat(gauge).isNotNull().extracting(Gauge::value).isEqualTo(0.0);
    }

    @Test
    void should_decrement_in_flight_counter_on_failure() {
        // given
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);

        var chain = filter.filter(messages -> {
            throw new RuntimeException("Failed");
        });

        // when
        assertThatThrownBy(() -> chain.accept(List.of())).isInstanceOf(RuntimeException.class);

        // then
        var gauge = meterRegistry
                .find("sqs.messages.in_flight")
                .tag("queue", "test-queue")
                .gauge();

        assertThat(gauge).isNotNull().extracting(Gauge::value).isEqualTo(0.0);
    }

    @Test
    void should_record_processing_timer_with_success_status() {
        // given
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);
        var chain = filter.filter(messages -> {});

        // when
        chain.accept(List.of(aRadomMessage(), aRadomMessage()));

        // then
        var timer = meterRegistry
                .find("sqs.message.processing")
                .tag("queue", "test-queue")
                .tag("status", "success")
                .timer();

        assertThat(timer).isNotNull().extracting(Timer::count).isEqualTo(1L);
    }

    @Test
    void should_record_processing_timer_with_failure_status() {
        // given
        var filter = new ObservableSqsMessageFilter(queueName, meterRegistry);

        var chain = filter.filter(messages -> {
            throw new IllegalStateException("Error");
        });

        // when
        assertThatThrownBy(() -> chain.accept(List.of(aRadomMessage(), aRadomMessage())))
                .isInstanceOf(IllegalStateException.class);

        // then
        var timer = meterRegistry
                .find("sqs.message.processing")
                .tag("queue", "test-queue")
                .tag("status", "failure")
                .tag("error", "IllegalStateException")
                .timer();

        assertThat(timer).isNotNull().extracting(Timer::count).isEqualTo(1L);
    }

    Message aRadomMessage() {
        return Message.builder()
                .messageId("msg-" + Math.random())
                .body("This is a test message")
                .receiptHandle("handle-" + Math.random())
                .build();
    }
}
