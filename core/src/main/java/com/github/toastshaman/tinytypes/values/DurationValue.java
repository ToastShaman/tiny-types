package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.Duration;
import java.util.function.Function;

public abstract class DurationValue extends AbstractValueType<Duration> {

    public DurationValue(Duration value) {
        this(value, AlwaysValid());
    }

    public DurationValue(Duration value, Validator<Duration> validator) {
        this(value, validator, Object::toString);
    }

    public DurationValue(Duration value, Validator<Duration> validator, Function<Duration, String> showFn) {
        super(value, validator, showFn);
    }
}
