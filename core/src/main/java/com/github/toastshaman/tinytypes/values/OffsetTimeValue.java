package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class OffsetTimeValue extends AbstractValueType<OffsetTimeValue> {
    public OffsetTimeValue(OffsetTimeValue value) {
        this(value, AlwaysValid());
    }

    public OffsetTimeValue(OffsetTimeValue value, Validator<OffsetTimeValue> validator) {
        super(value, validator, OffsetTimeValue::toString);
    }

    public OffsetTimeValue(
            OffsetTimeValue value, Validator<OffsetTimeValue> validator, Function<OffsetTimeValue, String> showFn) {
        super(value, validator, showFn);
    }
}
