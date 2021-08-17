package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class IntValue extends AbstractValueType<Integer> {
    public static IntValue ZERO = new IntValue(0);
    public static IntValue ONE = new IntValue(1);
    public static IntValue TWO = new IntValue(2);

    public IntValue(Integer value) {
        super(value);
    }

    public IntValue(Integer value, Validator<? super Integer> validator) {
        super(value, validator);
    }

    public IntValue(Integer value, Validator<? super Integer> validator, Function<Integer, String> showFn) {
        super(value, validator, showFn);
    }
}
