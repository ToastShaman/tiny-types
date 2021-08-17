package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class DoubleValue extends AbstractValueType<Double> implements Comparable<DoubleValue> {
    public static DoubleValue ZERO = new DoubleValue(0D);
    public static DoubleValue ONE = new DoubleValue(1D);
    public static DoubleValue TWO = new DoubleValue(2D);

    public DoubleValue(Double value) {
        super(value);
    }

    public DoubleValue(Double value, Validator<? super Double> validator) {
        super(value, validator);
    }

    public DoubleValue(Double value, Validator<? super Double> validator, Function<Double, String> showFn) {
        super(value, validator, showFn);
    }

    @Override
    public int compareTo(DoubleValue o) {
        return value.compareTo(o.value);
    }
}
