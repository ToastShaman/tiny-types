package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public class Firstname extends NonBlankStringValue {
    public Firstname(String value) {
        super(value, Validator.MaxLength(60), Object::toString);
    }
}
