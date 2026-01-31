package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.TracingMdcSqsMessageHandler.RestoringMdcScope.putClosable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.MDC;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class TracingMdcSqsMessageHandler<T> implements SqsMessagesHandler {

    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";

    private final ScopedValue<TraceId> scopedTraceId;
    private final ScopedValue<SpanId> scopedSpanId;
    private final Function<Message, T> handler;

    public TracingMdcSqsMessageHandler(Function<Message, T> handler) {
        this(TraceId.TRACE_ID, SpanId.SPAN_ID, handler);
    }

    public TracingMdcSqsMessageHandler(
            ScopedValue<TraceId> scopedTraceId, ScopedValue<SpanId> scopedSpanId, Function<Message, T> handler) {
        this.scopedTraceId = Objects.requireNonNull(scopedTraceId, "traceId must not be null");
        this.scopedSpanId = Objects.requireNonNull(scopedSpanId, "spanId must not be null");
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void accept(List<Message> messages) {
        for (var message : messages) {
            var traceId = scopedTraceId.orElseThrow(() -> new IllegalStateException("trace id must not be null"));
            var spanId = scopedSpanId.orElseThrow(() -> new IllegalStateException("span id must not be null"));

            try (var ignored1 = putClosable(MDC_TRACE_ID, traceId.unwrap())) {
                try (var ignored2 = putClosable(MDC_SPAN_ID, spanId.unwrap())) {
                    handler.apply(message);
                }
            }
        }
    }

    public static class RestoringMdcScope implements AutoCloseable {
        private final String key;
        private final String previous;

        public RestoringMdcScope(String key, String value) {
            this.key = Objects.requireNonNull(key, "key must not be null");
            this.previous = MDC.get(key);

            MDC.put(key, value);
        }

        @Override
        public void close() {
            if (previous == null) {
                MDC.remove(key);
            } else {
                MDC.put(key, previous);
            }
        }

        public static RestoringMdcScope putClosable(String key, String value) {
            return new RestoringMdcScope(key, value);
        }
    }
}
