package com.github.toastshaman.tinytypes.aws.sns;

import io.micrometer.tracing.Tracer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class TracingSnsPublisher<T> implements SnsPublisher<T> {

    private final SnsPublisher<T> delegate;

    private final Tracer tracer;

    private final TracingSnsHeaderPropagator propagator;

    public TracingSnsPublisher(SnsPublisher<T> delegate, Tracer tracer, TracingSnsHeaderPropagator propagator) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.propagator = Objects.requireNonNull(propagator, "propagator must not be null");
    }

    @Override
    public void publish(T message, Map<String, MessageAttributeValue> attributes) {
        var span = tracer.currentSpan();

        if (span == null) {
            delegate.publish(message, attributes);
            return;
        }

        var tracingAttributes = propagator.inject(span.context());

        Map<String, MessageAttributeValue> combined = new HashMap<>();
        combined.putAll(attributes);
        combined.putAll(tracingAttributes);

        delegate.publish(message, combined);
    }

    @Override
    public void publish(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        var span = tracer.currentSpan();

        if (span == null) {
            delegate.publish(messages, attributes);
            return;
        }

        var tracingAttributes = propagator.inject(span.context());

        Map<String, MessageAttributeValue> combined = new HashMap<>();
        combined.putAll(attributes);
        combined.putAll(tracingAttributes);

        delegate.publish(messages, combined);
    }
}
