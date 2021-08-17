package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.OffsetDateTime;
import java.util.function.Function;

public abstract class OffsetDateTimeValue extends AbstractValueType<OffsetDateTime> {
    public OffsetDateTimeValue(OffsetDateTime value) {
        this(value, AlwaysValid());
    }

    public OffsetDateTimeValue(OffsetDateTime value, Validator<OffsetDateTime> validator) {
        super(value, validator, OffsetDateTime::toString);
    }

    public OffsetDateTimeValue(
            OffsetDateTime value, Validator<OffsetDateTime> validator, Function<OffsetDateTime, String> showFn) {
        super(value, validator, showFn);
    }
}
