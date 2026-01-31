package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SpanId.SPAN_ID;
import static com.github.toastshaman.tinytypes.aws.sqs.TraceId.TRACE_ID;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class TracingSqsMessageHandler<T> implements SqsMessagesHandler {

    private final Function<Message, T> handler;

    private final Supplier<TraceId> traceIdSupplier;

    private final Supplier<SpanId> spanIdSupplier;

    public TracingSqsMessageHandler(Function<Message, T> handler) {
        this(handler, TraceId::random, SpanId::random);
    }

    public TracingSqsMessageHandler(
            Function<Message, T> handler, Supplier<TraceId> traceIdSupplier, Supplier<SpanId> spanIdSupplier) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
        this.traceIdSupplier = Objects.requireNonNull(traceIdSupplier, "traceIdSupplier must not be null");
        this.spanIdSupplier = Objects.requireNonNull(spanIdSupplier, "spanIdSupplier must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        for (Message message : messages) {
            var traceId = traceIdSupplier.get();
            var spanId = spanIdSupplier.get();

            ScopedValue.where(TRACE_ID, traceId).where(SPAN_ID, spanId).run(() -> handler.apply(message));
        }
    }
}
