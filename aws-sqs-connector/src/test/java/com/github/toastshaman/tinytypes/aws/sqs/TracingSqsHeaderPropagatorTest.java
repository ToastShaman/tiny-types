package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.test.simple.SimpleSpanBuilder;
import io.micrometer.tracing.test.simple.SimpleTraceContext;
import io.micrometer.tracing.test.simple.SimpleTracer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TracingSqsHeaderPropagatorTest {

    private final SimpleTracer tracer = new SimpleTracer();

    /**
     * A simple propagator for testing that injects/extracts traceId and spanId.
     */
    private final Propagator testPropagator = new Propagator() {
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

            return new SimpleSpanBuilder(tracer);
        }
    };

    @Test
    void inject_returns_empty_map() {
        var propagator = TracingSqsHeaderPropagator.noop();

        var span = tracer.nextSpan().name("test-span").start();
        var context = span.context();

        var attributes = propagator.inject(context);

        assertThat(attributes).isEmpty();
    }

    @Test
    void extract_returns_noop_span_builder() {
        var propagator = TracingSqsHeaderPropagator.noop();

        var spanBuilder = propagator.extract(Map.of());

        assertThat(spanBuilder).isNotNull();
    }

    @Test
    void throws_when_propagator_is_null() {
        assertThatNullPointerException()
                .isThrownBy(() -> TracingSqsHeaderPropagator.with(null))
                .withMessage("propagator must not be null");
    }

    @Test
    void inject_adds_tracing_headers_to_message_attributes() {
        var propagator = TracingSqsHeaderPropagator.with(testPropagator);

        var span = tracer.nextSpan().name("test-span").start();
        var context = span.context();

        var attributes = propagator.inject(context);

        assertThat(attributes).isNotEmpty();
        assertThat(attributes).containsKey("traceId");
        assertThat(attributes).containsKey("spanId");

        var traceIdAttribute = attributes.get("traceId");
        assertThat(traceIdAttribute.dataType()).isEqualTo("String");
        assertThat(traceIdAttribute.stringValue()).isEqualTo(context.traceId());

        var spanIdAttribute = attributes.get("spanId");
        assertThat(spanIdAttribute.dataType()).isEqualTo("String");
        assertThat(spanIdAttribute.stringValue()).isEqualTo(context.spanId());
    }

    @Test
    void extract_creates_span_builder_from_message_attributes() {
        var propagator = TracingSqsHeaderPropagator.with(testPropagator);

        var originalSpan = tracer.nextSpan().name("original-span").start();
        var originalContext = originalSpan.context();

        // Simulate attributes that would be on an incoming SQS message
        Map<String, MessageAttributeValue> attributes = Map.of(
                "traceId",
                MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(originalContext.traceId())
                        .build(),
                "spanId",
                MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(originalContext.spanId())
                        .build());

        var spanBuilder = propagator.extract(attributes);

        assertThat(spanBuilder).isNotNull();

        Span extractedSpan = spanBuilder.name("extracted-span").start();

        assertThat(extractedSpan.context().traceId()).isEqualTo(originalContext.traceId());
    }

    @Test
    void extract_handles_missing_attributes_gracefully() {
        var propagator = TracingSqsHeaderPropagator.with(testPropagator);

        Map<String, MessageAttributeValue> emptyAttributes = new HashMap<>();

        var spanBuilder = propagator.extract(emptyAttributes);

        assertThat(spanBuilder).isNotNull();

        // Should still be able to start a span, even if no trace context was extracted
        Span span = spanBuilder.name("new-span").start();
        assertThat(span).isNotNull();
    }

    @Test
    void roundtrip_preserves_trace_context() {
        var propagator = TracingSqsHeaderPropagator.with(testPropagator);

        // Create an original span
        var originalSpan = tracer.nextSpan().name("producer-span").start();
        var originalContext = originalSpan.context();

        // Inject context into message attributes (simulating sending a message)
        var attributes = propagator.inject(originalContext);

        // Extract context from message attributes (simulating receiving a message)
        var spanBuilder = propagator.extract(attributes);
        var consumerSpan = spanBuilder.name("consumer-span").start();

        // Verify the trace context is preserved across the roundtrip
        assertThat(consumerSpan.context().traceId()).isEqualTo(originalContext.traceId());
    }
}
