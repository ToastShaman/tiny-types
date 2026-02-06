package com.github.toastshaman.tinytypes.aws.sns;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

public interface TracingSnsHeaderPropagator {

    Map<String, MessageAttributeValue> inject(TraceContext context);

    static TracingSnsHeaderPropagator with(Propagator propagator) {
        Objects.requireNonNull(propagator, "propagator must not be null");

        return context -> {
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
        };
    }
}
