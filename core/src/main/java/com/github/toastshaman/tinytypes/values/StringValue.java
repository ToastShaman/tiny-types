package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.function.Function;

public class StringValue extends AbstractValueType<String> {

    public StringValue(String value) {
        super(value);
    }

    public StringValue(String value, Validator<? super String> validator) {
        super(value, validator);
    }

    public StringValue(String value, Validator<? super String> validator, Function<String, String> showFn) {
        super(value, validator, showFn);
    }
}
