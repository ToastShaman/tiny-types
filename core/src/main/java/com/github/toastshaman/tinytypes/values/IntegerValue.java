package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class IntegerValue extends AbstractValueType<Integer> {

    public IntegerValue(Integer value) {
        this(value, AlwaysValid());
    }

    public IntegerValue(Integer value, Validator<Integer> validator) {
        this(value, validator, Object::toString);
    }

    public IntegerValue(Integer value, Validator<Integer> validator, Function<Integer, String> showFn) {
        super(value, validator, showFn);
    }
}
