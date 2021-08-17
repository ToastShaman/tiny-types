package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class DoubleValue extends AbstractValueType<Double> {

    public DoubleValue(Double value) {
        this(value, AlwaysValid());
    }

    public DoubleValue(Double value, Validator<Double> validator) {
        this(value, validator, Object::toString);
    }

    public DoubleValue(Double value, Validator<Double> validator, Function<Double, String> showFn) {
        super(value, validator, showFn);
    }
}
