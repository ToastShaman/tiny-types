package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.test.simple.SimpleSpanBuilder;
import io.micrometer.tracing.test.simple.SimpleTraceContext;
import io.micrometer.tracing.test.simple.SimpleTracer;
import java.util.List;
import java.util.Objects;

public class FakePropagator implements Propagator {

    private final SimpleTracer tracer;

    public FakePropagator(SimpleTracer tracer) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
    }

    @Override
    public List<String> fields() {
        return List.of("traceId", "spanId");
    }

    @Override
    public <C> void inject(TraceContext context, C carrier, Setter<C> setter) {
        setter.set(carrier, "traceId", context.traceId());
        setter.set(carrier, "spanId", context.spanId());
    }

    @Override
    public <C> Span.Builder extract(C carrier, Getter<C> getter) {
        String traceId = getter.get(carrier, "traceId");
        String spanId = getter.get(carrier, "spanId");

        if (traceId != null && spanId != null) {
            SimpleTraceContext context = new SimpleTraceContext();
            context.setTraceId(traceId);
            context.setSpanId(spanId);
            return new SimpleSpanBuilder(tracer).setParent(context);
        }

        var context = Objects.requireNonNull(tracer.currentTraceContext().context());

        return tracer.spanBuilder().setParent(context);
    }
}
