package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.Function;

public abstract class ZonedDateTimeValue extends AbstractValueType<ZonedDateTime> {

    public ZonedDateTimeValue(ZonedDateTime value) {
        this(value, AlwaysValid());
    }

    public ZonedDateTimeValue(ZonedDateTime value, Validator<ZonedDateTime> validator) {
        this(value, validator, Objects::toString);
    }

    public ZonedDateTimeValue(
            ZonedDateTime value, Validator<ZonedDateTime> validator, Function<ZonedDateTime, String> showFn) {
        super(value, validator, showFn);
    }
}
