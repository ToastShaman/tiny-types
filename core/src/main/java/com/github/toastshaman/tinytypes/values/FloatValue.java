package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class FloatValue extends AbstractValueType<Float> {

    public static FloatValue ZERO = FloatValue.of(0F);
    public static FloatValue ONE = FloatValue.of(1F);
    public static FloatValue TWO = FloatValue.of(2F);
    public static FloatValue TEN = FloatValue.of(10F);

    public FloatValue(Float value, Validator<Float> validator, Function<Float, String> showFn) {
        super(value, validator, showFn);
    }

    public FloatValue inc() {
        return plus(FloatValue.ONE);
    }

    public FloatValue dec() {
        return minus(FloatValue.ONE);
    }

    public FloatValue plus(FloatValue other) {
        return map(it -> it + other.value, FloatValue::new);
    }

    public FloatValue minus(FloatValue other) {
        return map(it -> it - other.value, FloatValue::new);
    }

    public FloatValue times(FloatValue other) {
        return map(it -> it * other.value, FloatValue::new);
    }

    public FloatValue div(FloatValue other) {
        return map(it -> it / other.value, FloatValue::new);
    }

    public FloatValue rem(FloatValue other) {
        return map(it -> it % other.value, FloatValue::new);
    }

    public static FloatValue of(Float value) {
        return new FloatValue(value, Validator.AlwaysValid(), Object::toString);
    }
}
