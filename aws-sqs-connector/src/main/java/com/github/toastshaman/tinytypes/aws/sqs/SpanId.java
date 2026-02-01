package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;
import io.opentelemetry.sdk.trace.IdGenerator;
import java.util.Optional;

public final class SpanId extends NonBlankStringValue {

    public static final ScopedValue<SpanId> SPAN_ID = ScopedValue.newInstance();

    public SpanId(String value) {
        super(value);
    }

    public static SpanId of(String value) {
        return new SpanId(value);
    }

    public static SpanId random() {
        return new SpanId(IdGenerator.random().generateSpanId());
    }

    public static Optional<SpanId> getCurrent() {
        return Optional.ofNullable(SPAN_ID.isBound() ? SPAN_ID.get() : null);
    }
}
