package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.LocalTime;
import java.util.function.Function;

public class LongValue extends AbstractValueType<Long> {

    public static LongValue ZERO = LongValue.of(0L);
    public static LongValue ONE = LongValue.of(1L);
    public static LongValue TWO = LongValue.of(2L);
    public static LongValue TEN = LongValue.of(10L);

    public LongValue(Long value, Validator<Long> validator, Function<Long, String> showFn) {
        super(value, validator, showFn);
    }

    public LongValue inc() {
        return plus(LongValue.ONE);
    }

    public LongValue dec() {
        return minus(LongValue.ONE);
    }

    public LongValue plus(LongValue other) {
        return map(it -> it + other.value, LongValue::new);
    }

    public LongValue minus(LongValue other) {
        return map(it -> it - other.value, LongValue::new);
    }

    public LongValue times(LongValue other) {
        return map(it -> it * other.value, LongValue::new);
    }

    public LongValue div(LongValue other) {
        return map(it -> it / other.value, LongValue::new);
    }

    public LongValue rem(LongValue other) {
        return map(it -> it % other.value, LongValue::new);
    }

    public static LongValue of(Long value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static LongValue of(Long value, Validator<Long> validator) {
        return of(value, validator, Object::toString);
    }

    public static LongValue of(Long value, Function<Long, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static LongValue of(Long value,
                               Validator<Long> validator,
                               Function<Long, String> show) {
        return new LongValue(value, validator, show);
    }
}
