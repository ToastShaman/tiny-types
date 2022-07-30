package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.Objects;
import java.util.function.Function;

public class NonEmptyStringValue extends StringValue {

    public NonEmptyStringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, Validator.NonEmpty().and(validator), showFn);
    }

    public static NonEmptyStringValue of(String value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static NonEmptyStringValue of(String value, Validator<String> validator) {
        return of(value, validator, Object::toString);
    }

    public static NonEmptyStringValue of(String value, Function<String, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static NonEmptyStringValue of(String value,
                                         Validator<String> validator,
                                         Function<String, String> show) {
        return new NonEmptyStringValue(value, validator, show);
    }
}
