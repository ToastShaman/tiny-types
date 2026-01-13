package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ObservableSqsMessageFilterTest {

    List<Message> messages = List.of(
            Message.builder().messageId("1").body("a").build(),
            Message.builder().messageId("2").body("b").build());

    Message message = Message.builder().messageId("1").body("a").build();

    @Test
    void increments_received_counter_by_message_count() {
        // given
        var registry = new SimpleMeterRegistry();
        var queueUrl = new QueueUrl("https://example.com/test-queue");
        var filter = new ObservableSqsMessageFilter(queueUrl, registry);
        var filtered = filter.filter(_ -> {});

        // when
        filtered.handle(messages);

        // then
        Counter counter = registry.counter("sqs.messages.received", "queueUrl", queueUrl.asString());
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void records_processing_time() {
        // given
        var registry = new SimpleMeterRegistry();
        var queueUrl = new QueueUrl("https://example.com/test-queue");
        var filter = new ObservableSqsMessageFilter(queueUrl, registry);
        var messages = List.of(message);
        var filtered = filter.filter(_ -> {});

        // when
        filtered.handle(messages);

        // then
        Timer timer = registry.timer("sqs.message.processing.time", "queueUrl", queueUrl.asString());
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.totalTime(TimeUnit.NANOSECONDS)).isGreaterThanOrEqualTo(0);
    }

    @Test
    void increments_error_counter_when_handler_throws() {
        // given
        var registry = new SimpleMeterRegistry();
        var queueUrl = new QueueUrl("https://example.com/test-queue");
        var filter = new ObservableSqsMessageFilter(queueUrl, registry);
        var filtered = filter.filter(_ -> {
            throw new RuntimeException("boom!");
        });

        // when + then
        assertThatThrownBy(() -> filtered.handle(messages))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom!");

        Counter counter = registry.counter("sqs.message.processing.errors", "queueUrl", queueUrl.asString());
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void still_records_timer_when_handler_throws() {
        // given
        var registry = new SimpleMeterRegistry();
        var queueUrl = new QueueUrl("https://example.com/test-queue");
        var filter = new ObservableSqsMessageFilter(queueUrl, registry);
        var messages = List.of(Message.builder().messageId("1").body("a").build());
        var filtered = filter.filter(_ -> {
            throw new RuntimeException("fail");
        });

        // when + then
        assertThatThrownBy(() -> filtered.handle(messages)).isInstanceOf(RuntimeException.class);

        Timer timer = registry.timer("sqs.message.processing.time", "queueUrl", queueUrl.asString());
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    void handles_empty_message_list() {
        // given
        var registry = new SimpleMeterRegistry();
        var queueUrl = new QueueUrl("https://example.com/test-queue");
        var filter = new ObservableSqsMessageFilter(queueUrl, registry);
        var called = new AtomicBoolean(false);
        var filtered = filter.filter(_ -> called.set(true));

        // when
        filtered.handle(List.of());

        // then
        assertThat(called).isTrue();

        Counter counter = registry.counter("sqs.messages.received", "queueUrl", queueUrl.asString());
        assertThat(counter.count()).isEqualTo(0.0);
    }
}
