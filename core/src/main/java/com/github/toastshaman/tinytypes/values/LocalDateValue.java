package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;

public abstract class LocalDateValue extends AbstractValueType<LocalDate> {

    public LocalDateValue(LocalDate value) {
        this(value, AlwaysValid());
    }

    public LocalDateValue(LocalDate value, Validator<LocalDate> validator) {
        this(value, validator, Objects::toString);
    }

    public LocalDateValue(LocalDate value, Validator<LocalDate> validator, Function<LocalDate, String> showFn) {
        super(value, validator, showFn);
    }
}
