package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public class Hobby extends NonBlankStringValue {
    public Hobby(String value) {
        super(value, Validator.AlwaysValid(), Object::toString);
    }
}
