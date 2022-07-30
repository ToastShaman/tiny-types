package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.Instant;
import java.util.function.Function;

public class InstantValue extends AbstractValueType<Instant> {

    public InstantValue(Instant value, Validator<Instant> validator, Function<Instant, String> showFn) {
        super(value, validator, showFn);
    }

    public static InstantValue of(Instant value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static InstantValue of(Instant value, Validator<Instant> validator) {
        return of(value, validator, Object::toString);
    }

    public static InstantValue of(Instant value, Function<Instant, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static InstantValue of(Instant value,
                                  Validator<Instant> validator,
                                  Function<Instant, String> show) {
        return new InstantValue(value, validator, show);
    }
}
