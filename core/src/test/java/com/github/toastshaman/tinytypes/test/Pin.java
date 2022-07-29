package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.obfuscators.Obfuscate;
import com.github.toastshaman.tinytypes.values.NonBlankStringValue;

public class Pin extends NonBlankStringValue {
    public Pin(String value) {
        super(value, Obfuscate.keepLast(3));
    }
}
