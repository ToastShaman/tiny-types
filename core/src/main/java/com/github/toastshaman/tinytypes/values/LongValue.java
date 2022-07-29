package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class LongValue extends AbstractValueType<Long> implements Comparable<LongValue> {
    public static LongValue ZERO = new LongValue(0L);
    public static LongValue ONE = new LongValue(1L);
    public static LongValue TWO = new LongValue(2L);

    public LongValue(Long value) {
        super(value);
    }

    public LongValue(Long value, Validator<? super Long> validator) {
        super(value, validator);
    }

    public LongValue(Long value, Validator<? super Long> validator, Function<Long, String> showFn) {
        super(value, validator, showFn);
    }

    @Override
    public int compareTo(LongValue o) {
        return value.compareTo(o.value);
    }
}
