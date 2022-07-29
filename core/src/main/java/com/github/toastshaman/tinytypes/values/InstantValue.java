package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.time.Instant;
import java.util.function.Function;

public class InstantValue extends AbstractValueType<Instant> {

    public InstantValue(Instant value, Validator<Instant> validator, Function<Instant, String> showFn) {
        super(value, validator, showFn);
    }
}
