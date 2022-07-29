package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class IntegerValue extends AbstractValueType<Integer> {
    public static IntegerValue ZERO = new IntegerValue(0);
    public static IntegerValue ONE = new IntegerValue(1);
    public static IntegerValue TWO = new IntegerValue(2);
    public static IntegerValue TEN = new IntegerValue(10);

    public IntegerValue(Integer value) {
        super(value);
    }

    public IntegerValue(Integer value, Validator<? super Integer> validator) {
        super(value, validator);
    }

    public IntegerValue(Integer value, Validator<? super Integer> validator, Function<Integer, String> showFn) {
        super(value, validator, showFn);
    }
}
