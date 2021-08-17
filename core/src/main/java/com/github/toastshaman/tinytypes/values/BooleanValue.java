package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BooleanValue extends AbstractValueType<Boolean> {

    public BooleanValue(Boolean value) {
        this(value, AlwaysValid());
    }

    public BooleanValue(Boolean value, Validator<Boolean> validator) {
        this(value, validator, Object::toString);
    }

    public BooleanValue(Boolean value, Validator<Boolean> validator, Function<Boolean, String> showFn) {
        super(value, validator, showFn);
    }

    public boolean isTrue() {
        return value;
    }

    public boolean isFalse() {
        return !value;
    }

    public void onTrue(Runnable r) {
        if (isTrue()) {
            r.run();
        }
    }

    public void onFalse(Runnable r) {
        if (isFalse()) {
            r.run();
        }
    }

    public <T> T fold(Supplier<T> truthyFn, Supplier<T> falsyFn) {
        return isTrue() ? truthyFn.get() : falsyFn.get();
    }
}
