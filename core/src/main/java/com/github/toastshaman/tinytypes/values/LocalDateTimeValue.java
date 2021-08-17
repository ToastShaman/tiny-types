package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

public abstract class LocalDateTimeValue extends AbstractValueType<LocalDateTime> {

    public LocalDateTimeValue(LocalDateTime value) {
        this(value, AlwaysValid());
    }

    public LocalDateTimeValue(LocalDateTime value, Validator<LocalDateTime> validator) {
        this(value, validator, Objects::toString);
    }

    public LocalDateTimeValue(
            LocalDateTime value, Validator<LocalDateTime> validator, Function<LocalDateTime, String> showFn) {
        super(value, validator, showFn);
    }
}
