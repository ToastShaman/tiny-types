package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TracingSqsMessageHandlerTest {

    @Test
    void should_bind_trace_id_and_span_id_for_each_message() {
        var capturedTraceIds = new ArrayList<TraceId>();
        var capturedSpanIds = new ArrayList<SpanId>();

        var handler = new TracingSqsMessageHandler<>(message -> {
            TraceId.getCurrent().ifPresent(capturedTraceIds::add);
            SpanId.getCurrent().ifPresent(capturedSpanIds::add);
            return null;
        });

        var messages = List.of(
                Message.builder().messageId("1").body("first").build(),
                Message.builder().messageId("2").body("second").build(),
                Message.builder().messageId("3").body("third").build());

        handler.accept(messages);

        assertThat(capturedTraceIds).hasSize(3);
        assertThat(capturedSpanIds).hasSize(3);
    }

    @Test
    void should_generate_unique_trace_and_span_ids_for_each_message() {
        var capturedTraceIds = new ArrayList<TraceId>();
        var capturedSpanIds = new ArrayList<SpanId>();

        var handler = new TracingSqsMessageHandler<>(message -> {
            TraceId.getCurrent().ifPresent(capturedTraceIds::add);
            SpanId.getCurrent().ifPresent(capturedSpanIds::add);
            return null;
        });

        var messages = List.of(
                Message.builder().messageId("1").body("first").build(),
                Message.builder().messageId("2").body("second").build());

        handler.accept(messages);

        assertThat(capturedTraceIds.get(0)).isNotEqualTo(capturedTraceIds.get(1));
        assertThat(capturedSpanIds.get(0)).isNotEqualTo(capturedSpanIds.get(1));
    }

    @Test
    void should_generate_trace_format_ids() {
        var capturedTraceId = new AtomicReference<TraceId>();
        var capturedSpanId = new AtomicReference<SpanId>();

        var handler = new TracingSqsMessageHandler<>(message -> {
            TraceId.getCurrent().ifPresent(capturedTraceId::set);
            SpanId.getCurrent().ifPresent(capturedSpanId::set);
            return null;
        });

        handler.accept(List.of(Message.builder().messageId("1").body("test").build()));

        assertThat(capturedTraceId.get().unwrap()).hasSize(32);
        assertThat(capturedSpanId.get().unwrap()).hasSize(16);
    }

    @Test
    void should_use_custom_id_suppliers_when_provided() {
        var expectedTraceId = TraceId.of("custom-trace-id");
        var expectedSpanId = SpanId.of("custom-span-id");

        var capturedTraceId = new AtomicReference<TraceId>();
        var capturedSpanId = new AtomicReference<SpanId>();

        var handler = new TracingSqsMessageHandler<>(
                message -> {
                    TraceId.getCurrent().ifPresent(capturedTraceId::set);
                    SpanId.getCurrent().ifPresent(capturedSpanId::set);
                    return null;
                },
                () -> expectedTraceId,
                () -> expectedSpanId);

        handler.accept(List.of(Message.builder().messageId("1").body("test").build()));

        assertThat(capturedTraceId.get()).isEqualTo(expectedTraceId);
        assertThat(capturedSpanId.get()).isEqualTo(expectedSpanId);
    }

    @Test
    void should_unbind_trace_context_after_processing_each_message() {
        var handler = new TracingSqsMessageHandler<>(message -> {
            assertThat(TraceId.TRACE_ID.isBound()).isTrue();
            assertThat(SpanId.SPAN_ID.isBound()).isTrue();
            return null;
        });

        handler.accept(List.of(Message.builder().messageId("1").body("test").build()));

        assertThat(TraceId.TRACE_ID.isBound()).isFalse();
        assertThat(SpanId.SPAN_ID.isBound()).isFalse();
    }

    @Test
    void should_unbind_trace_context_even_when_handler_throws() {
        var handler = new TracingSqsMessageHandler<>(message -> {
            throw new RuntimeException("Processing failed");
        });

        var messages = List.of(Message.builder().messageId("1").body("test").build());

        assertThatThrownBy(() -> handler.accept(messages)).isInstanceOf(RuntimeException.class);

        assertThat(TraceId.TRACE_ID.isBound()).isFalse();
        assertThat(SpanId.SPAN_ID.isBound()).isFalse();
    }

    @Test
    void should_process_messages_and_return_results_via_handler() {
        var processedBodies = new ArrayList<String>();

        var handler = new TracingSqsMessageHandler<>(message -> {
            processedBodies.add(message.body());
            return message.body().toUpperCase();
        });

        var messages = List.of(
                Message.builder().messageId("1").body("hello").build(),
                Message.builder().messageId("2").body("world").build());

        handler.accept(messages);

        assertThat(processedBodies).containsExactly("hello", "world");
    }
}
