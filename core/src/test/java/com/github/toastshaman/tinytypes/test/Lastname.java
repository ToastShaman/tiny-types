package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public class Lastname extends NonBlankStringValue {
    public Lastname(String value) {
        super(value, Validator.MaxLength(60));
    }
}
