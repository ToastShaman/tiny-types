package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.obfuscators.Obfuscate;
import com.github.toastshaman.tinytypes.validation.Validator;

public abstract class SecretStringValue extends NonBlankStringValue {

    public SecretStringValue(String value) {
        super(value, Validator.AlwaysValid(), Obfuscate.fully());
    }
}
