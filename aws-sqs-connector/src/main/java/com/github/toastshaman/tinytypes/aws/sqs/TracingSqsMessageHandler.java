package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public final class TracingSqsMessageHandler<T> implements SqsMessagesHandler {

    private final Tracer tracer;

    private final Propagator propagator;

    private final Function<Message, T> handler;

    public TracingSqsMessageHandler(Tracer tracer, Propagator propagator, Function<Message, T> handler) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.propagator = Objects.requireNonNull(propagator, "propagator must not be null");
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void accept(List<Message> messages) {
        for (Message message : messages) {
            var attributes = message.messageAttributes();

            var span = propagator
                    .extract(attributes, (c, key) -> {
                        MessageAttributeValue attr = c.get(key);
                        return attr != null ? attr.stringValue() : null;
                    })
                    .name("processing-sqs-message")
                    .start();

            try (var ws = tracer.withSpan(span)) {
                handler.apply(message);
            } catch (Exception e) {
                span.error(e);
                throw e;
            } finally {
                span.end();
            }
        }
    }
}
