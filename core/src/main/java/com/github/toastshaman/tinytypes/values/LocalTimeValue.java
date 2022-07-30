package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

public class LocalTimeValue extends AbstractValueType<LocalTime> {

    public LocalTimeValue(LocalTime value, Validator<LocalTime> validator, Function<LocalTime, String> showFn) {
        super(value, validator, showFn);
    }

    public static LocalTimeValue of(LocalTime value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static LocalTimeValue of(LocalTime value, Validator<LocalTime> validator) {
        return of(value, validator, Object::toString);
    }

    public static LocalTimeValue of(LocalTime value, Function<LocalTime, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static LocalTimeValue of(LocalTime value,
                                    Validator<LocalTime> validator,
                                    Function<LocalTime, String> show) {
        return new LocalTimeValue(value, validator, show);
    }
}
