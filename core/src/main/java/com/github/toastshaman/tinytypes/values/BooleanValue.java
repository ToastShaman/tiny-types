package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class BooleanValue extends AbstractValueType<Boolean> {
    public static BooleanValue TRUE = new BooleanValue(true);
    public static BooleanValue FALSE = new BooleanValue(false);

    public BooleanValue(Boolean value) {
        super(value);
    }

    public BooleanValue(Boolean value, Validator<? super Boolean> validator) {
        super(value, validator);
    }

    public BooleanValue(Boolean value, Function<Boolean, String> showFn) {
        super(value, showFn);
    }

    public BooleanValue(Boolean value, Validator<? super Boolean> validator, Function<Boolean, String> showFn) {
        super(value, validator, showFn);
    }

    public BooleanValue not() {
        return new BooleanValue(!value, validator, showFn);
    }

    public Boolean isTrue() {
        return value;
    }

    public boolean isFalse() {
        return !value;
    }
}
