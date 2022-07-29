package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class DoubleValue extends AbstractValueType<Double> {

    public static DoubleValue ZERO = DoubleValue.of(0D);
    public static DoubleValue ONE = DoubleValue.of(1D);
    public static DoubleValue TWO = DoubleValue.of(2D);
    public static DoubleValue TEN = DoubleValue.of(10D);

    public DoubleValue(Double value, Validator<Double> validator, Function<Double, String> showFn) {
        super(value, validator, showFn);
    }

    public DoubleValue inc() {
        return plus(DoubleValue.ONE);
    }

    public DoubleValue dec() {
        return minus(DoubleValue.ONE);
    }

    public DoubleValue plus(DoubleValue other) {
        return map(it -> it + other.value, DoubleValue::new);
    }

    public DoubleValue minus(DoubleValue other) {
        return map(it -> it - other.value, DoubleValue::new);
    }

    public DoubleValue times(DoubleValue other) {
        return map(it -> it * other.value, DoubleValue::new);
    }

    public DoubleValue div(DoubleValue other) {
        return map(it -> it / other.value, DoubleValue::new);
    }

    public DoubleValue rem(DoubleValue other) {
        return map(it -> it % other.value, DoubleValue::new);
    }

    public static DoubleValue of(Double value) {
        return new DoubleValue(value, Validator.AlwaysValid(), Object::toString);
    }
}
