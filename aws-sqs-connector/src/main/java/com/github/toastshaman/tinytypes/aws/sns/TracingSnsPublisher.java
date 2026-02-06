package com.github.toastshaman.tinytypes.aws.sns;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public class TracingSnsPublisher<T> implements SnsPublisher<T> {

    private final SnsPublisher<T> delegate;

    private final Tracer tracer;

    private final Propagator propagator;

    public TracingSnsPublisher(SnsPublisher<T> delegate, Tracer tracer, Propagator propagator) {
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

        var allAttributes = new HashMap<>(attributes);

        propagator.inject(span.context(), allAttributes, (carrier, key, value) -> {
            Objects.requireNonNull(carrier)
                    .put(
                            key,
                            MessageAttributeValue.builder()
                                    .stringValue(value)
                                    .dataType("String")
                                    .build());
        });

        return Map.copyOf(allAttributes);
    }
}
