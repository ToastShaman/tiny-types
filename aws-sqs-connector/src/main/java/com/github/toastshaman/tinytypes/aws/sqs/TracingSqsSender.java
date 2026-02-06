package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public class TracingSqsSender<T> implements SqsSender<T> {

    private final SqsSender<T> delegate;

    private final Tracer tracer;

    private final Propagator propagator;

    public TracingSqsSender(SqsSender<T> delegate, Tracer tracer, Propagator propagator) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.propagator = Objects.requireNonNull(propagator, "propagator must not be null");
    }

    @Override
    public void send(T message, Map<String, MessageAttributeValue> attributes) {
        delegate.send(message, withTracingHeaders(attributes));
    }

    @Override
    public void send(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        delegate.send(messages, withTracingHeaders(attributes));
    }

    private Map<String, MessageAttributeValue> withTracingHeaders(Map<String, MessageAttributeValue> attributes) {
        var span = tracer.currentSpan();

        if (span == null) {
            return attributes;
        }

        var carrier = new HashMap<>(attributes);

        propagator.inject(span.context(), carrier, (c, key, value) -> {
            Objects.requireNonNull(c)
                    .put(
                            key,
                            MessageAttributeValue.builder()
                                    .stringValue(value)
                                    .dataType("String")
                                    .build());
        });

        return Map.copyOf(carrier);
    }
}
