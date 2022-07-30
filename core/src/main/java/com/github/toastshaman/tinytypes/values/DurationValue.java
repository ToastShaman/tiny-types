package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.Duration;
import java.util.function.Function;

public class DurationValue extends AbstractValueType<Duration> {

    public DurationValue(Duration value, Validator<Duration> validator, Function<Duration, String> showFn) {
        super(value, validator, showFn);
    }

    public static DurationValue of(Duration value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static DurationValue of(Duration value, Validator<Duration> validator) {
        return of(value, validator, Object::toString);
    }

    public static DurationValue of(Duration value, Function<Duration, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static DurationValue of(Duration value,
                                   Validator<Duration> validator,
                                   Function<Duration, String> show) {
        return new DurationValue(value, validator, show);
    }
}
