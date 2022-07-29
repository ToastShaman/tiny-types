package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class StringValue extends AbstractValueType<String> {

    public StringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, validator, showFn);
    }

    public static StringValue of(String value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static StringValue of(String value, Validator<String> validator) {
        return of(value, validator, Object::toString);
    }

    public static StringValue of(String value, Function<String, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static StringValue of(String value, Validator<String> validator, Function<String, String> showFn) {
        return new StringValue(value, validator, showFn);
    }
}
