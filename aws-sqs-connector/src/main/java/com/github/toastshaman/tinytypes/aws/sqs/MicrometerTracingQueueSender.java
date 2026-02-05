package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Tracer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public class MicrometerTracingQueueSender<T> implements SqsSender<T> {

    private final SqsSender<T> delegate;

    private final Tracer tracer;

    private final MicrometerSqsTracingPropagator propagator;

    public MicrometerTracingQueueSender(
            SqsSender<T> delegate, Tracer tracer, MicrometerSqsTracingPropagator propagator) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.propagator = Objects.requireNonNull(propagator, "propagator must not be null");
    }

    @Override
    public void send(T message, Map<String, MessageAttributeValue> attributes) {
        var context = tracer.currentTraceContext().context();

        var tracingAttributes = propagator.inject(context);

        Map<String, MessageAttributeValue> combined = new HashMap<>();
        combined.putAll(attributes);
        combined.putAll(tracingAttributes);

        delegate.send(message, combined);
    }

    @Override
    public void send(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        var context = tracer.currentTraceContext().context();

        var tracingAttributes = propagator.inject(context);

        Map<String, MessageAttributeValue> combined = new HashMap<>();
        combined.putAll(attributes);
        combined.putAll(tracingAttributes);

        delegate.send(messages, combined);
    }
}
