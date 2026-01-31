package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TracingSqsMessageFilterTest {

    @Test
    void should_bind_trace_id_and_span_id_during_message_processing() {
        var filter = new TracingSqsMessageFilter();
        var capturedTraceId = new AtomicReference<TraceId>();
        var capturedSpanId = new AtomicReference<SpanId>();

        SqsMessagesHandler handler = messages -> {
            capturedTraceId.set(TraceId.getCurrent().orElse(null));
            capturedSpanId.set(SpanId.getCurrent().orElse(null));
        };

        var decoratedHandler = filter.filter(handler);

        decoratedHandler.handle(List.of());

        assertThat(capturedTraceId.get()).isNotNull();
        assertThat(capturedSpanId.get()).isNotNull();
        assertThat(capturedTraceId.get().unwrap()).isNotBlank();
        assertThat(capturedSpanId.get().unwrap()).isNotBlank();
    }

    @Test
    void should_generate_ulid_format_ids() {
        var filter = new TracingSqsMessageFilter();
        var capturedTraceId = new AtomicReference<TraceId>();
        var capturedSpanId = new AtomicReference<SpanId>();

        SqsMessagesHandler handler = messages -> {
            TraceId.getCurrent().ifPresent(capturedTraceId::set);
            SpanId.getCurrent().ifPresent(capturedSpanId::set);
        };
        var decoratedHandler = filter.filter(handler);

        decoratedHandler.handle(List.of());

        // ULIDs are 26 characters long
        assertThat(capturedTraceId.get().unwrap()).hasSize(26);
        assertThat(capturedSpanId.get().unwrap()).hasSize(26);
    }

    @Test
    void should_use_custom_id_suppliers_when_provided() {
        var expectedTraceId = TraceId.of("custom-trace-id");
        var expectedSpanId = SpanId.of("custom-span-id");

        var filter = new TracingSqsMessageFilter(() -> expectedTraceId, () -> expectedSpanId);
        var capturedTraceId = new AtomicReference<TraceId>();
        var capturedSpanId = new AtomicReference<SpanId>();

        SqsMessagesHandler handler = messages -> {
            TraceId.getCurrent().ifPresent(capturedTraceId::set);
            SpanId.getCurrent().ifPresent(capturedSpanId::set);
        };
        var decoratedHandler = filter.filter(handler);

        decoratedHandler.handle(List.of());

        assertThat(capturedTraceId.get()).isEqualTo(expectedTraceId);
        assertThat(capturedSpanId.get()).isEqualTo(expectedSpanId);
    }

    @Test
    void should_unbind_trace_context_after_processing_completes() {
        var filter = new TracingSqsMessageFilter();

        SqsMessagesHandler handler = messages -> {
            assertThat(TraceId.TRACE_ID.isBound()).isTrue();
            assertThat(SpanId.SPAN_ID.isBound()).isTrue();
        };
        var decoratedHandler = filter.filter(handler);

        decoratedHandler.handle(List.of());

        assertThat(TraceId.TRACE_ID.isBound()).isFalse();
        assertThat(SpanId.SPAN_ID.isBound()).isFalse();
    }

    @Test
    void should_unbind_trace_context_even_when_handler_throws() {
        var filter = new TracingSqsMessageFilter();

        SqsMessagesHandler handler = messages -> {
            throw new RuntimeException("Processing failed");
        };
        var decoratedHandler = filter.filter(handler);

        assertThatThrownBy(() -> decoratedHandler.handle(List.of())).isInstanceOf(RuntimeException.class);

        assertThat(TraceId.TRACE_ID.isBound()).isFalse();
        assertThat(SpanId.SPAN_ID.isBound()).isFalse();
    }

    @Test
    void should_generate_different_ids_for_each_invocation() {
        var filter = new TracingSqsMessageFilter();
        var firstTraceId = new AtomicReference<TraceId>();
        var firstSpanId = new AtomicReference<SpanId>();
        var secondTraceId = new AtomicReference<TraceId>();
        var secondSpanId = new AtomicReference<SpanId>();

        SqsMessagesHandler firstHandler = messages -> {
            TraceId.getCurrent().ifPresent(firstTraceId::set);
            SpanId.getCurrent().ifPresent(firstSpanId::set);
        };
        var decoratedFirstHandler = filter.filter(firstHandler);

        SqsMessagesHandler secondHandler = messages -> {
            TraceId.getCurrent().ifPresent(secondTraceId::set);
            SpanId.getCurrent().ifPresent(secondSpanId::set);
        };
        var decoratedSecondHandler = filter.filter(secondHandler);

        decoratedFirstHandler.handle(List.of());
        decoratedSecondHandler.handle(List.of());

        assertThat(firstTraceId.get()).isNotEqualTo(secondTraceId.get());
        assertThat(firstSpanId.get()).isNotEqualTo(secondSpanId.get());
    }

    @Test
    void should_reject_null_trace_id_supplier() {
        assertThatThrownBy(() -> new TracingSqsMessageFilter(null, SpanId::random))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("traceIdSupplier must not be null");
    }

    @Test
    void should_reject_null_span_id_supplier() {
        assertThatThrownBy(() -> new TracingSqsMessageFilter(TraceId::random, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("spanIdSupplier must not be null");
    }
}
