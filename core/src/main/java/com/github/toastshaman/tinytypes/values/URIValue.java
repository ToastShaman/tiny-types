package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.net.URI;
import java.util.function.Function;

public abstract class URIValue extends AbstractValueType<URI> {

    public URIValue(URI value) {
        this(value, AlwaysValid());
    }

    public URIValue(URI value, Validator<URI> validator) {
        this(value, validator, URI::toString);
    }

    public URIValue(URI value, Validator<URI> validator, Function<URI, String> showFn) {
        super(value, validator, showFn);
    }
}
