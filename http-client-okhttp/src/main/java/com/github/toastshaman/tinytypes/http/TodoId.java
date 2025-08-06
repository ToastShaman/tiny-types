package com.github.toastshaman.tinytypes.http;

import com.github.toastshaman.tinytypes.validation.Validator;
import com.github.toastshaman.tinytypes.values.IntegerValue;

public final class TodoId extends IntegerValue {

    public TodoId(Integer value) {
        super(value, Validator.Min(0));
    }

    public static TodoId of(int i) {
        return new TodoId(i);
    }
}
