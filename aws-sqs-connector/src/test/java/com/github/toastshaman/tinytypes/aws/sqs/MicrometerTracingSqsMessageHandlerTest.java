package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.tracing.test.simple.SimpleTracer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MicrometerTracingSqsMessageHandlerTest {

    private final SimpleTracer tracer = new SimpleTracer();

    @Test
    void creates_a_span_when_processing_messages() {
        var hasBeenCalled = new AtomicBoolean(false);

        var handler = new MicrometerTracingSqsMessageHandler<Void>(tracer, msg -> {
            hasBeenCalled.set(true);

            var currentSpan = tracer.currentSpan();

            assertThat(currentSpan).isNotNull();
            assertThat(currentSpan.isNoop()).isFalse();

            var traceId = tracer.currentTraceContext().context().traceId();

            assertThat(traceId).isNotEmpty();

            return null;
        });

        handler.accept(List.of(Message.builder().body("hello").build()));

        assertThat(hasBeenCalled).isTrue();
    }
}
