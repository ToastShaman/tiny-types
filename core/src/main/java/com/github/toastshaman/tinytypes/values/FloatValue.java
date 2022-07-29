package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class FloatValue extends AbstractValueType<Float> {
    public static FloatValue ZERO = new FloatValue(0F);
    public static FloatValue ONE = new FloatValue(1F);
    public static FloatValue TWO = new FloatValue(2F);
    public static FloatValue TEN = new FloatValue(10F);

    public FloatValue(Float value) {
        super(value);
    }

    public FloatValue(Float value, Validator<? super Float> validator) {
        super(value, validator);
    }

    public FloatValue(Float value, Function<Float, String> showFn) {
        super(value, showFn);
    }

    public FloatValue(Float value, Validator<? super Float> validator, Function<Float, String> showFn) {
        super(value, validator, showFn);
    }

    public FloatValue inc() {
        return new FloatValue(value + 1, validator, showFn);
    }

    public FloatValue dec() {
        return new FloatValue(value - 1, validator, showFn);
    }

    public FloatValue plus(FloatValue other) {
        return new FloatValue(value + other.value, validator, showFn);
    }

    public FloatValue minus(FloatValue other) {
        return new FloatValue(value - other.value, validator, showFn);
    }

    public FloatValue times(FloatValue other) {
        return new FloatValue(value * other.value, validator, showFn);
    }

    public FloatValue div(FloatValue other) {
        return new FloatValue(value / other.value, validator, showFn);
    }

    public FloatValue rem(FloatValue other) {
        return new FloatValue(value % other.value, validator, showFn);
    }
}
