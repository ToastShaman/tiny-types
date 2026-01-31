package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SpanId.SPAN_ID;
import static com.github.toastshaman.tinytypes.aws.sqs.TraceId.TRACE_ID;

import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class TracingSqsMessageFilter implements SqsMessagesFilter {

    private final Supplier<TraceId> traceIdSupplier;

    private final Supplier<SpanId> spanIdSupplier;

    public TracingSqsMessageFilter() {
        this(TraceId::random, SpanId::random);
    }

    public TracingSqsMessageFilter(Supplier<TraceId> traceIdSupplier, Supplier<SpanId> spanIdSupplier) {
        this.traceIdSupplier = Objects.requireNonNull(traceIdSupplier, "traceIdSupplier must not be null");
        this.spanIdSupplier = Objects.requireNonNull(spanIdSupplier, "spanIdSupplier must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            var traceId = traceIdSupplier.get();
            var spanId = spanIdSupplier.get();

            ScopedValue.where(TRACE_ID, traceId).where(SPAN_ID, spanId).run(() -> next.handle(messages));
        };
    }
}
