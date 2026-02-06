package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public interface TracingSqsHeaderPropagator {

    Map<String, MessageAttributeValue> inject(TraceContext context);

    Span.Builder extract(Map<String, MessageAttributeValue> attributes);

    static TracingSqsHeaderPropagator noop() {
        return with(Propagator.NOOP);
    }

    static TracingSqsHeaderPropagator with(Propagator propagator) {
        Objects.requireNonNull(propagator, "propagator must not be null");

        return new TracingSqsHeaderPropagator() {
            @Override
            public Map<String, MessageAttributeValue> inject(TraceContext context) {
                Map<String, MessageAttributeValue> attributes = new HashMap<>();

                propagator.inject(context, attributes, (carrier, key, value) -> {
                    Objects.requireNonNull(carrier, "carrier must not be null");
                    carrier.put(
                            key,
                            MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue(value)
                                    .build());
                });

                return Map.copyOf(attributes);
            }

            @Override
            public Span.Builder extract(Map<String, MessageAttributeValue> attributes) {
                return propagator.extract(attributes, (carrier, key) -> {
                    Objects.requireNonNull(carrier, "carrier must not be null");
                    MessageAttributeValue value = carrier.get(key);
                    return value != null ? value.stringValue() : null;
                });
            }
        };
    }
}
