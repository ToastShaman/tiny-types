package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

public class LocalTimeValue extends AbstractValueType<LocalTime> {

    public LocalTimeValue(LocalTime value) {
        super(value);
    }

    public LocalTimeValue(LocalTime value, Validator<? super LocalTime> validator) {
        super(value, validator);
    }

    public LocalTimeValue(LocalTime value, Function<LocalTime, String> showFn) {
        super(value, showFn);
    }

    public LocalTimeValue(LocalTime value, Validator<? super LocalTime> validator, Function<LocalTime, String> showFn) {
        super(value, validator, showFn);
    }
}
