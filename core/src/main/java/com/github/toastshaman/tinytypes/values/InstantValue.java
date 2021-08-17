package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.time.Instant;
import java.util.function.Function;

public abstract class InstantValue extends AbstractValueType<Instant> {

    public InstantValue(Instant value) {
        this(value, AlwaysValid());
    }

    public InstantValue(Instant value, Validator<Instant> validator) {
        this(value, validator, Object::toString);
    }

    public InstantValue(Instant value, Validator<Instant> validator, Function<Instant, String> showFn) {
        super(value, validator, showFn);
    }
}
