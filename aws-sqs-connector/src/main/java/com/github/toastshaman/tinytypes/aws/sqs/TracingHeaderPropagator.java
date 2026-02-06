package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public interface TracingHeaderPropagator<T> {

    Map<String, T> inject(TraceContext context);

    Span.Builder extract(Map<String, T> attributes);

    static TracingHeaderPropagator<MessageAttributeValue> sqs(Propagator propagator) {
        return with(
                propagator,
                value -> software.amazon.awssdk.services.sqs.model.MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value)
                        .build(),
                software.amazon.awssdk.services.sqs.model.MessageAttributeValue::stringValue);
    }

    static TracingHeaderPropagator<software.amazon.awssdk.services.sns.model.MessageAttributeValue> sns(
            Propagator propagator) {
        return with(
                propagator,
                value -> software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(value)
                        .build(),
                software.amazon.awssdk.services.sns.model.MessageAttributeValue::stringValue);
    }

    static <T> TracingHeaderPropagator<T> with(
            Propagator propagator, Function<String, T> encode, Function<T, String> decode) {
        Objects.requireNonNull(propagator, "propagator must not be null");

        return new TracingHeaderPropagator<T>() {
            @Override
            public Map<String, T> inject(TraceContext context) {
                Map<String, T> attributes = new HashMap<>();

                propagator.inject(context, attributes, (carrier, key, value) -> {
                    Objects.requireNonNull(carrier, "carrier must not be null");
                    carrier.put(key, encode.apply(value));
                });

                return Map.copyOf(attributes);
            }

            @Override
            public Span.Builder extract(Map<String, T> attributes) {
                return propagator.extract(attributes, (carrier, key) -> {
                    Objects.requireNonNull(carrier, "carrier must not be null");
                    T value = carrier.get(key);
                    return value != null ? decode.apply(value) : null;
                });
            }
        };
    }
}
