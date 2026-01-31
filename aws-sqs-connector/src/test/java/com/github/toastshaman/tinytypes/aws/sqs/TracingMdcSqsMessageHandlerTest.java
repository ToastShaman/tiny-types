package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SpanId.SPAN_ID;
import static com.github.toastshaman.tinytypes.aws.sqs.TraceId.TRACE_ID;
import static com.github.toastshaman.tinytypes.aws.sqs.TracingMdcSqsMessageHandler.MDC_SPAN_ID;
import static com.github.toastshaman.tinytypes.aws.sqs.TracingMdcSqsMessageHandler.MDC_TRACE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TracingMdcSqsMessageHandlerTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void should_set_mdc_trace_id_and_span_id_from_scoped_values() {
        var capturedMdcTraceId = new AtomicReference<String>();
        var capturedMdcSpanId = new AtomicReference<String>();

        var handler = new TracingMdcSqsMessageHandler<>(message -> {
            capturedMdcTraceId.set(MDC.get(MDC_TRACE_ID));
            capturedMdcSpanId.set(MDC.get(MDC_SPAN_ID));
            return null;
        });

        var traceId = TraceId.of("test-trace-id");
        var spanId = SpanId.of("test-span-id");

        ScopedValue.where(TRACE_ID, traceId)
                .where(SPAN_ID, spanId)
                .run(() -> handler.handle(
                        List.of(Message.builder().messageId("1").body("test").build())));

        assertThat(capturedMdcTraceId.get()).isEqualTo("test-trace-id");
        assertThat(capturedMdcSpanId.get()).isEqualTo("test-span-id");
    }

    @Test
    void should_restore_previous_mdc_values_after_processing() {
        MDC.put(MDC_TRACE_ID, "previous-trace-id");
        MDC.put(MDC_SPAN_ID, "previous-span-id");

        var handler = new TracingMdcSqsMessageHandler<>(message -> null);

        var traceId = TraceId.of("new-trace-id");
        var spanId = SpanId.of("new-span-id");

        ScopedValue.where(TRACE_ID, traceId)
                .where(SPAN_ID, spanId)
                .run(() -> handler.handle(
                        List.of(Message.builder().messageId("1").body("test").build())));

        assertThat(MDC.get(MDC_TRACE_ID)).isEqualTo("previous-trace-id");
        assertThat(MDC.get(MDC_SPAN_ID)).isEqualTo("previous-span-id");
    }

    @Test
    void should_clear_mdc_when_no_previous_values_existed() {
        var handler = new TracingMdcSqsMessageHandler<>(message -> null);

        var traceId = TraceId.of("test-trace-id");
        var spanId = SpanId.of("test-span-id");

        ScopedValue.where(TRACE_ID, traceId)
                .where(SPAN_ID, spanId)
                .run(() -> handler.handle(
                        List.of(Message.builder().messageId("1").body("test").build())));

        assertThat(MDC.get(MDC_TRACE_ID)).isNull();
        assertThat(MDC.get(MDC_SPAN_ID)).isNull();
    }

    @Test
    void should_restore_mdc_even_when_handler_throws() {
        MDC.put(MDC_TRACE_ID, "previous-trace-id");
        MDC.put(MDC_SPAN_ID, "previous-span-id");

        var handler = new TracingMdcSqsMessageHandler<>(message -> {
            throw new RuntimeException("Processing failed");
        });

        var traceId = TraceId.of("test-trace-id");
        var spanId = SpanId.of("test-span-id");
        var messages = List.of(Message.builder().messageId("1").body("test").build());

        assertThatThrownBy(() -> ScopedValue.where(TRACE_ID, traceId)
                        .where(SPAN_ID, spanId)
                        .run(() -> handler.handle(messages)))
                .isInstanceOf(RuntimeException.class);

        assertThat(MDC.get(MDC_TRACE_ID)).isEqualTo("previous-trace-id");
        assertThat(MDC.get(MDC_SPAN_ID)).isEqualTo("previous-span-id");
    }

    @Test
    void should_not_set_mdc_when_scoped_values_not_bound() {
        var capturedMdcTraceId = new AtomicReference<String>();
        var capturedMdcSpanId = new AtomicReference<String>();

        var handler = new TracingMdcSqsMessageHandler<>(message -> {
            capturedMdcTraceId.set(MDC.get(MDC_TRACE_ID));
            capturedMdcSpanId.set(MDC.get(MDC_SPAN_ID));
            return null;
        });

        handler.handle(List.of(Message.builder().messageId("1").body("test").build()));

        assertThat(capturedMdcTraceId.get()).isNull();
        assertThat(capturedMdcSpanId.get()).isNull();
    }

    @Test
    void should_process_multiple_messages_with_mdc_context() {
        var capturedTraceIds = new ArrayList<String>();
        var capturedSpanIds = new ArrayList<String>();

        var handler = new TracingMdcSqsMessageHandler<>(message -> {
            capturedTraceIds.add(MDC.get(MDC_TRACE_ID));
            capturedSpanIds.add(MDC.get(MDC_SPAN_ID));
            return null;
        });

        var traceId = TraceId.of("test-trace-id");
        var spanId = SpanId.of("test-span-id");

        var messages = List.of(
                Message.builder().messageId("1").body("first").build(),
                Message.builder().messageId("2").body("second").build());

        ScopedValue.where(TRACE_ID, traceId).where(SPAN_ID, spanId).run(() -> handler.handle(messages));

        assertThat(capturedTraceIds).containsExactly("test-trace-id", "test-trace-id");
        assertThat(capturedSpanIds).containsExactly("test-span-id", "test-span-id");
    }

    @Test
    void should_reject_null_handler() {
        assertThatThrownBy(() -> new TracingMdcSqsMessageHandler<>(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("handler must not be null");
    }
}
