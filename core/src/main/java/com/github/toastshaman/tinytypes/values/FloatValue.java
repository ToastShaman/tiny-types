package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class FloatValue extends AbstractValueType<Float> implements Comparable<FloatValue> {
    public static FloatValue ZERO = new FloatValue(0F);
    public static FloatValue ONE = new FloatValue(1F);
    public static FloatValue TWO = new FloatValue(2F);

    public FloatValue(Float value) {
        super(value);
    }

    public FloatValue(Float value, Validator<? super Float> validator) {
        super(value, validator);
    }

    public FloatValue(Float value, Validator<? super Float> validator, Function<Float, String> showFn) {
        super(value, validator, showFn);
    }

    @Override
    public int compareTo(FloatValue o) {
        return value.compareTo(o.value);
    }
}
