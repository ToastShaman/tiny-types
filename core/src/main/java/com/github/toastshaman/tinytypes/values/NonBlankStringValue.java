package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class NonBlankStringValue extends StringValue {

    public NonBlankStringValue(String value) {
        super(value, Validator.NonBlank());
    }

    public NonBlankStringValue(String value, Validator<String> validator) {
        super(value, Validator.NonBlank().and(validator));
    }

    public NonBlankStringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, Validator.NonBlank().and(validator), showFn);
    }
}
