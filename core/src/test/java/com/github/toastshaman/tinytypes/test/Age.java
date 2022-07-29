package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.IntegerValue;

public class Age extends IntegerValue {
    public Age(Integer value) {
        super(value, Validator.Min(1).and(Validator.Max(120)));
    }
}
