package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.LocalDate;
import java.util.function.Function;

public class LocalDateValue extends AbstractValueType<LocalDate> {

    public LocalDateValue(LocalDate value, Validator<LocalDate> validator, Function<LocalDate, String> showFn) {
        super(value, validator, showFn);
    }

    public static LocalDateValue of(LocalDate value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static LocalDateValue of(LocalDate value, Validator<LocalDate> validator) {
        return of(value, validator, Object::toString);
    }

    public static LocalDateValue of(LocalDate value, Function<LocalDate, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static LocalDateValue of(LocalDate value,
                                    Validator<LocalDate> validator,
                                    Function<LocalDate, String> show) {
        return new LocalDateValue(value, validator, show);
    }
}
