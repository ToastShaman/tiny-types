package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class NonEmptyStringValue extends StringValue {

    public NonEmptyStringValue(String value) {
        super(value, Validator.NonEmpty());
    }

    public NonEmptyStringValue(String value, Function<String, String> showFn) {
        super(value, Validator.NonEmpty(), showFn);
    }

    public NonEmptyStringValue(String value, Validator<String> validator) {
        super(value, Validator.NonEmpty().and(validator));
    }

    public NonEmptyStringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, Validator.NonEmpty().and(validator), showFn);
    }
}
