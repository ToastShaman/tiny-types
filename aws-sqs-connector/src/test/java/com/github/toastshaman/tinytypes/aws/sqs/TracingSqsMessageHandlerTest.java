package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.test.simple.SimpleTracer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TracingSqsMessageHandlerTest {

    SimpleTracer tracer = new SimpleTracer();

    Propagator propagator = new FakePropagator(tracer);

    @Test
    void creates_a_span_when_processing_messages() {
        var hasBeenCalled = new AtomicBoolean(false);

        var chain = new TracingSqsMessageFilter(tracer)
                .andThen(new TracingSqsMessageHandler<Void>(tracer, propagator, msg -> {
                    hasBeenCalled.set(true);

                    var span = tracer.currentSpan();
                    assertThat(span).isNotNull();
                    assertThat(span.context().traceId()).isNotBlank();
                    assertThat(span.context().spanId()).isNotBlank();

                    return null;
                }));

        chain.accept(List.of(Message.builder().body("hello").build()));

        assertThat(hasBeenCalled).isTrue();
    }

    @Test
    void copies_a_span_when_processing_messages() {
        var hasBeenCalled = new AtomicBoolean(false);

        var handler = new TracingSqsMessageHandler<Void>(tracer, propagator, msg -> {
            hasBeenCalled.set(true);

            var span = tracer.currentSpan();
            assertThat(span).isNotNull();
            assertThat(span.context().traceId()).isEqualTo("test-trace-id");
            assertThat(span.context().spanId()).isNotEqualTo("test-span-id");

            return null;
        });

        Message message = Message.builder()
                .body("hello")
                .messageAttributes(Map.of(
                        "traceId",
                                MessageAttributeValue.builder()
                                        .stringValue("test-trace-id")
                                        .dataType("String")
                                        .build(),
                        "spanId",
                                MessageAttributeValue.builder()
                                        .stringValue("test-span-id")
                                        .dataType("String")
                                        .build()))
                .build();

        handler.accept(List.of(message));

        assertThat(hasBeenCalled).isTrue();
    }
}
