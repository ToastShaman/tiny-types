package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.Objects;
import java.util.function.Supplier;

public final class TracingSqsMessageFilter implements SqsMessagesFilter {

    private final ScopedValue<TraceId> scopedTraceId;
    private final Supplier<TraceId> traceIdSupplier;

    private final ScopedValue<SpanId> scopedSpanId;
    private final Supplier<SpanId> spanIdSupplier;

    public TracingSqsMessageFilter() {
        this(TraceId.TRACE_ID, TraceId::random, SpanId.SPAN_ID, SpanId::random);
    }

    public TracingSqsMessageFilter(Supplier<TraceId> traceIdSupplier, Supplier<SpanId> spanIdSupplier) {
        this(TraceId.TRACE_ID, traceIdSupplier, SpanId.SPAN_ID, spanIdSupplier);
    }

    public TracingSqsMessageFilter(
            ScopedValue<TraceId> scopedTraceId,
            Supplier<TraceId> traceIdSupplier,
            ScopedValue<SpanId> scopedSpanId,
            Supplier<SpanId> spanIdSupplier) {
        this.scopedTraceId = Objects.requireNonNull(scopedTraceId, "scopedTraceId must not be null");
        this.traceIdSupplier = Objects.requireNonNull(traceIdSupplier, "traceIdSupplier must not be null");
        this.scopedSpanId = Objects.requireNonNull(scopedSpanId, "scopedSpanId must not be null");
        this.spanIdSupplier = Objects.requireNonNull(spanIdSupplier, "spanIdSupplier must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            var traceId = traceIdSupplier.get();
            var spanId = spanIdSupplier.get();

            ScopedValue.where(scopedTraceId, traceId)
                    .where(scopedSpanId, spanId)
                    .run(() -> next.accept(messages));
        };
    }
}
