package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Tracer;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class MicrometerTracingSqsMessageHandler<T> implements SqsMessagesHandler {

    private final Tracer tracer;

    private final Function<Message, T> handler;

    public MicrometerTracingSqsMessageHandler(Tracer tracer, Function<Message, T> handler) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void accept(List<Message> messages) {
        for (Message message : messages) {
            var span = tracer.nextSpan().start();
            try (var ws = tracer.withSpan(span)) {
                handler.apply(message);
            } finally {
                span.end();
            }
        }
    }
}
