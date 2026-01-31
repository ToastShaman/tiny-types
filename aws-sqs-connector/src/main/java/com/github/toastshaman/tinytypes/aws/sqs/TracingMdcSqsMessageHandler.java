package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.MDC;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class TracingMdcSqsMessageHandler<T> implements SqsMessagesHandler {

    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";

    private final Function<Message, T> handler;

    public TracingMdcSqsMessageHandler(Function<Message, T> handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        for (Message message : messages) {
            var previousTraceId = MDC.get(MDC_TRACE_ID);
            var previousSpanId = MDC.get(MDC_SPAN_ID);

            try {
                TraceId.getCurrent().map(TraceId::unwrap).ifPresent(traceId -> MDC.put(MDC_TRACE_ID, traceId));
                SpanId.getCurrent().map(SpanId::unwrap).ifPresent(spanId -> MDC.put(MDC_SPAN_ID, spanId));

                handler.apply(message);
            } finally {
                restoreMdcValue(MDC_TRACE_ID, previousTraceId);
                restoreMdcValue(MDC_SPAN_ID, previousSpanId);
            }
        }
    }

    private void restoreMdcValue(String key, String previousValue) {
        if (previousValue == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, previousValue);
        }
    }
}
