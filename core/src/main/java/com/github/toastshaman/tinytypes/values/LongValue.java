package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class LongValue extends AbstractValueType<Long> {

    public LongValue(Long value) {
        this(value, AlwaysValid());
    }

    public LongValue(Long value, Validator<Long> validator) {
        this(value, validator, Object::toString);
    }

    public LongValue(Long value, Validator<Long> validator, Function<Long, String> showFn) {
        super(value, validator, showFn);
    }
}
