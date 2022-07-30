package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class NonBlankStringValue extends StringValue {

    public NonBlankStringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, Validator.NonBlank().and(validator), showFn);
    }

    public static NonBlankStringValue of(String value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static NonBlankStringValue of(String value, Validator<String> validator) {
        return of(value, validator, Object::toString);
    }

    public static NonBlankStringValue of(String value, Function<String, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static NonBlankStringValue of(String value,
                                         Validator<String> validator,
                                         Function<String, String> show) {
        return new NonBlankStringValue(value, validator, show);
    }
}
