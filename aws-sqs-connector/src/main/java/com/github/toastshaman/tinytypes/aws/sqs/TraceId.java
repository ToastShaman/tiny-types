package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;
import io.opentelemetry.sdk.trace.IdGenerator;
import java.util.Optional;

public final class TraceId extends NonBlankStringValue {

    public static final ScopedValue<TraceId> TRACE_ID = ScopedValue.newInstance();

    public TraceId(String value) {
        super(value);
    }

    public static TraceId of(String value) {
        return new TraceId(value);
    }

    public static TraceId random() {
        return TRACE_ID.orElse(new TraceId(IdGenerator.random().generateSpanId()));
    }

    public static Optional<TraceId> getCurrent() {
        return Optional.ofNullable(TRACE_ID.isBound() ? TRACE_ID.get() : null);
    }
}
