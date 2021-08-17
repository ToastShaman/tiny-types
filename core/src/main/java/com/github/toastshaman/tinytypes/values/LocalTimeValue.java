package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.LocalTime;
import java.util.function.Function;

public abstract class LocalTimeValue extends AbstractValueType<LocalTime> {

    public LocalTimeValue(LocalTime value) {
        this(value, AlwaysValid());
    }

    public LocalTimeValue(LocalTime value, Validator<LocalTime> validator) {
        this(value, validator, Object::toString);
    }

    public LocalTimeValue(LocalTime value, Validator<LocalTime> validator, Function<LocalTime, String> showFn) {
        super(value, validator, showFn);
    }
}
