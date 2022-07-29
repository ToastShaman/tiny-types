package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.LocalTime;
import java.util.function.Function;

public class LocalTimeValue extends AbstractValueType<LocalTime> {

    public LocalTimeValue(LocalTime value, Validator<LocalTime> validator, Function<LocalTime, String> showFn) {
        super(value, validator, showFn);
    }
}
