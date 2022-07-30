package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class BooleanValue extends AbstractValueType<Boolean> {
    public static BooleanValue TRUE = BooleanValue.of(true);
    public static BooleanValue FALSE = BooleanValue.of(false);

    public BooleanValue(Boolean value, Validator<Boolean> validator, Function<Boolean, String> showFn) {
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


    public static BooleanValue of(Boolean value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static BooleanValue of(Boolean value, Validator<Boolean> validator) {
        return of(value, validator, Object::toString);
    }

    public static BooleanValue of(Boolean value, Function<Boolean, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static BooleanValue of(Boolean value,
                                  Validator<Boolean> validator,
                                  Function<Boolean, String> show) {
        return new BooleanValue(value, validator, show);
    }
}
