package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class IntegerValue extends AbstractValueType<Integer> {

    public static IntegerValue ZERO = IntegerValue.of(0);
    public static IntegerValue ONE = IntegerValue.of(1);
    public static IntegerValue TWO = IntegerValue.of(2);
    public static IntegerValue TEN = IntegerValue.of(10);

    public IntegerValue(Integer value, Validator<Integer> validator, Function<Integer, String> showFn) {
        super(value, validator, showFn);
    }

    public IntegerValue inc() {
        return plus(IntegerValue.ONE);
    }

    public IntegerValue dec() {
        return minus(IntegerValue.ONE);
    }

    public IntegerValue plus(IntegerValue other) {
        return map(it -> it + other.value, IntegerValue::new);
    }

    public IntegerValue minus(IntegerValue other) {
        return map(it -> it - other.value, IntegerValue::new);
    }

    public IntegerValue times(IntegerValue other) {
        return map(it -> it * other.value, IntegerValue::new);
    }

    public IntegerValue div(IntegerValue other) {
        return map(it -> it / other.value, IntegerValue::new);
    }

    public IntegerValue rem(IntegerValue other) {
        return map(it -> it % other.value, IntegerValue::new);
    }

    public static IntegerValue of(Integer value) {
        return new IntegerValue(value, Validator.AlwaysValid(), Object::toString);
    }
}
