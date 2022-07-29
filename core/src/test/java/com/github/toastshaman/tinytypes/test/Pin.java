package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.obfuscators.Obfuscate;
import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public class Pin extends NonBlankStringValue {
    public Pin(String value) {
        super(value, Validator.AlwaysValid(), Obfuscate.keepLast(3));
    }
}
