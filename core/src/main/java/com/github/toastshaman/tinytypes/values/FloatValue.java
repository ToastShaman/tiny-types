package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class FloatValue extends AbstractValueType<Float> {

    public FloatValue(Float value) {
        this(value, AlwaysValid());
    }

    public FloatValue(Float value, Validator<Float> validator) {
        this(value, validator, Object::toString);
    }

    public FloatValue(Float value, Validator<Float> validator, Function<Float, String> showFn) {
        super(value, validator, showFn);
    }
}
