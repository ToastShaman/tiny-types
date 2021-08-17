package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.NonBlank;

import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class NonBlankStringValue extends StringValue {

    public NonBlankStringValue(String value) {
        this(value, NonBlank());
    }

    public NonBlankStringValue(String value, Validator<String> validator) {
        this(value, NonBlank().and(validator), String::toString);
    }

    public NonBlankStringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, NonBlank().and(validator), showFn);
    }
}
