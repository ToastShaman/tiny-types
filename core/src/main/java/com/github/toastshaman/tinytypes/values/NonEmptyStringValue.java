package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.NonEmpty;

import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class NonEmptyStringValue extends StringValue {

    public NonEmptyStringValue(String value) {
        this(value, NonEmpty());
    }

    public NonEmptyStringValue(String value, Validator<String> validator) {
        this(value, NonEmpty().and(validator), String::toString);
    }

    public NonEmptyStringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, NonEmpty().and(validator), showFn);
    }
}
