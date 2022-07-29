package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.LocalDate;
import java.util.function.Function;

public class LocalDateValue extends AbstractValueType<LocalDate> {

    public LocalDateValue(LocalDate value) {
        super(value);
    }

    public LocalDateValue(LocalDate value, Validator<? super LocalDate> validator) {
        super(value, validator);
    }

    public LocalDateValue(LocalDate value, Function<LocalDate, String> showFn) {
        super(value, showFn);
    }

    public LocalDateValue(LocalDate value, Validator<? super LocalDate> validator, Function<LocalDate, String> showFn) {
        super(value, validator, showFn);
    }
}
