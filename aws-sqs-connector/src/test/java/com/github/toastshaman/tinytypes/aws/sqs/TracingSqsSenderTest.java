package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.test.simple.SimpleSpanBuilder;
import io.micrometer.tracing.test.simple.SimpleTraceContext;
import io.micrometer.tracing.test.simple.SimpleTracer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TracingSqsSenderTest {

    SimpleTracer tracer = new SimpleTracer();

    Propagator testPropagator = new Propagator() {
        @Override
        public List<String> fields() {
            return List.of("traceId", "spanId");
        }

        @Override
        public <C> void inject(TraceContext context, C carrier, Setter<C> setter) {
            setter.set(carrier, "traceId", context.traceId());
            setter.set(carrier, "spanId", context.spanId());
        }

        @Override
        public <C> Span.Builder extract(C carrier, Getter<C> getter) {
            String traceId = getter.get(carrier, "traceId");
            String spanId = getter.get(carrier, "spanId");

            if (traceId != null && spanId != null) {
                SimpleTraceContext context = new SimpleTraceContext();
                context.setTraceId(traceId);
                context.setSpanId(spanId);
                return new SimpleSpanBuilder(tracer).setParent(context);
            }

            return tracer.spanBuilder();
        }
    };

    TracingSqsHeaderPropagator propagator = TracingSqsHeaderPropagator.with(testPropagator);

    @Test
    void adds_tracing_attributes_when_sending_single_message() {
        var capturingDelegate = new CapturingSqsSender<String>();
        var sender = new TracingSqsSender<>(capturingDelegate, tracer, propagator);

        var span = tracer.nextSpan().name("test-span").start();
        try (var ignored = tracer.withSpan(span)) {
            sender.send("hello", Map.of());
        }

        assertThat(capturingDelegate.singleMessages).hasSize(1);

        var captured = capturingDelegate.singleMessages.getFirst();
        assertThat(captured.message()).isEqualTo("hello");
        assertThat(captured.attributes()).containsKey("traceId");
        assertThat(captured.attributes()).containsKey("spanId");
        assertThat(captured.attributes())
                .extractingByKey("traceId")
                .extracting(MessageAttributeValue::stringValue)
                .isEqualTo(span.context().traceId());
        assertThat(captured.attributes())
                .extractingByKey("spanId")
                .extracting(MessageAttributeValue::stringValue)
                .isEqualTo(span.context().spanId());
    }

    @Test
    void preserves_existing_attributes_when_sending_single_message() {
        var capturingDelegate = new CapturingSqsSender<String>();
        var sender = new TracingSqsSender<>(capturingDelegate, tracer, propagator);

        var existingAttribute = MessageAttributeValue.builder()
                .dataType("String")
                .stringValue("existing-value")
                .build();

        var span = tracer.nextSpan().name("test-span").start();
        try (var ignored = tracer.withSpan(span)) {
            sender.send("hello", Map.of("x-custom-header", existingAttribute));
        }

        var captured = capturingDelegate.singleMessages.getFirst();
        assertThat(captured.attributes()).containsKey("x-custom-header");
        assertThat(captured.attributes())
                .extractingByKey("x-custom-header")
                .extracting(MessageAttributeValue::stringValue)
                .isEqualTo("existing-value");
        assertThat(captured.attributes()).containsKey("traceId");
        assertThat(captured.attributes()).containsKey("spanId");
    }

    @Test
    void adds_tracing_attributes_when_sending_batch() {
        var capturingDelegate = new CapturingSqsSender<String>();
        var sender = new TracingSqsSender<>(capturingDelegate, tracer, propagator);

        var span = tracer.nextSpan().name("test-span").start();
        try (var ignored = tracer.withSpan(span)) {
            sender.send(List.of("msg1", "msg2", "msg3"), Map.of());
        }

        assertThat(capturingDelegate.batchMessages).hasSize(1);

        var captured = capturingDelegate.batchMessages.getFirst();
        assertThat(captured.messages()).containsExactly("msg1", "msg2", "msg3");
        assertThat(captured.attributes()).containsKey("traceId");
        assertThat(captured.attributes()).containsKey("spanId");
        assertThat(captured.attributes())
                .extractingByKey("traceId")
                .extracting(MessageAttributeValue::stringValue)
                .isEqualTo(span.context().traceId());
    }

    @Test
    void preserves_existing_attributes_when_sending_batch() {
        var capturingDelegate = new CapturingSqsSender<String>();
        var sender = new TracingSqsSender<>(capturingDelegate, tracer, propagator);

        var existingAttribute = MessageAttributeValue.builder()
                .dataType("String")
                .stringValue("batch-value")
                .build();

        var span = tracer.nextSpan().name("test-span").start();
        try (var ignored = tracer.withSpan(span)) {
            sender.send(List.of("msg1"), Map.of("x-batch-header", existingAttribute));
        }

        var captured = capturingDelegate.batchMessages.getFirst();
        assertThat(captured.attributes()).containsKey("x-batch-header");
        assertThat(captured.attributes())
                .extractingByKey("x-batch-header")
                .extracting(MessageAttributeValue::stringValue)
                .isEqualTo("batch-value");
        assertThat(captured.attributes()).containsKey("traceId");
    }

    /**
     * A simple capturing implementation of SqsSender for testing purposes.
     * No mocking required - just captures the sent messages and their attributes.
     */
    static class CapturingSqsSender<T> implements SqsSender<T> {
        final List<CapturedSingleMessage<T>> singleMessages = new ArrayList<>();
        final List<CapturedBatchMessage<T>> batchMessages = new ArrayList<>();

        @Override
        public void send(T message, Map<String, MessageAttributeValue> attributes) {
            singleMessages.add(new CapturedSingleMessage<>(message, attributes));
        }

        @Override
        public void send(List<T> messages, Map<String, MessageAttributeValue> attributes) {
            batchMessages.add(new CapturedBatchMessage<>(messages, attributes));
        }

        record CapturedSingleMessage<T>(T message, Map<String, MessageAttributeValue> attributes) {}

        record CapturedBatchMessage<T>(List<T> messages, Map<String, MessageAttributeValue> attributes) {}
    }
}
