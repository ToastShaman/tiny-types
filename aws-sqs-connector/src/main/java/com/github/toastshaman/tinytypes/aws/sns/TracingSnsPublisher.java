package com.github.toastshaman.tinytypes.aws.sns;

import com.github.toastshaman.tinytypes.aws.sqs.TracingHeaderPropagator;
import io.micrometer.tracing.Tracer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class TracingSnsPublisher<T> implements SnsPublisher<T> {

    private final SnsPublisher<T> delegate;

    private final Tracer tracer;

    private final TracingHeaderPropagator<MessageAttributeValue> propagator;

    public TracingSnsPublisher(
            SnsPublisher<T> delegate, Tracer tracer, TracingHeaderPropagator<MessageAttributeValue> propagator) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.propagator = Objects.requireNonNull(propagator, "propagator must not be null");
    }

    @Override
    public void publish(T message, Map<String, MessageAttributeValue> attributes) {
        delegate.publish(message, withTracingHeaders(attributes));
    }

    @Override
    public void publish(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        delegate.publish(messages, withTracingHeaders(attributes));
    }

    private Map<String, MessageAttributeValue> withTracingHeaders(Map<String, MessageAttributeValue> attributes) {
        var span = tracer.currentSpan();
        if (span == null) {
            return attributes;
        }

        var combined = new HashMap<>(attributes);
        combined.putAll(propagator.inject(span.context()));
        return combined;
    }
}
