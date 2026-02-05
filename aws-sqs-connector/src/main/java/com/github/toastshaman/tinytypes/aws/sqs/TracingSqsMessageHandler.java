package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Tracer;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

public final class TracingSqsMessageHandler<T> implements SqsMessagesHandler {

    private final Tracer tracer;

    private final TracingSqsHeaderPropagator propagator;

    private final Function<Message, T> handler;

    public TracingSqsMessageHandler(
            Tracer tracer, TracingSqsHeaderPropagator propagator, Function<Message, T> handler) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.propagator = Objects.requireNonNull(propagator, "propagator must not be null");
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void accept(List<Message> messages) {
        for (Message message : messages) {
            var attributes = message.messageAttributes();
            var hasPropagationHeaders = propagator.fields().stream().allMatch(attributes::containsKey);
            var span = hasPropagationHeaders
                    ? propagator.extract(attributes).start()
                    : tracer.nextSpan().start();

            try (var ws = tracer.withSpan(span)) {
                handler.apply(message);
            } finally {
                span.end();
            }
        }
    }
}
