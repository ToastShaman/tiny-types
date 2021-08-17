package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;

public abstract class StringValue extends AbstractValueType<String> {

    public StringValue(String value) {
        this(value, AlwaysValid());
    }

    public StringValue(String value, Validator<String> validator) {
        this(value, validator, String::toString);
    }

    public StringValue(String value, Validator<String> validator, Function<String, String> showFn) {
        super(value, validator, showFn);
    }

    public <T extends StringValue> boolean equals(T other) {
        return value.equals(other.value);
    }

    public <T extends StringValue> boolean equalsIgnoreCase(T other) {
        return value.equalsIgnoreCase(other.value);
    }

    public boolean isBlank() {
        return value.isBlank();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public boolean startsWith(String prefix) {
        return value.startsWith(prefix);
    }

    public boolean endsWith(String suffix) {
        return value.endsWith(suffix);
    }
}
