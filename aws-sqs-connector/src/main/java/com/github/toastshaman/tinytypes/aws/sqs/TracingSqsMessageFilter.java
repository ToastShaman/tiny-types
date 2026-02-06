package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Tracer;
import java.util.Objects;

public class TracingSqsMessageFilter implements SqsMessagesFilter {

    private final Tracer tracer;

    public TracingSqsMessageFilter(Tracer tracer) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            var span = tracer.nextSpan().start();
            try (var ws = tracer.withSpan(span)) {
                next.accept(messages);
            } catch (Exception e) {
                span.error(e);
                throw e;
            } finally {
                span.end();
            }
        };
    }
}
