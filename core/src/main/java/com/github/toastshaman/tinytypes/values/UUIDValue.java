package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.UUID;
import java.util.function.Function;

public abstract class UUIDValue extends AbstractValueType<UUID> {

    public UUIDValue(UUID value) {
        this(value, AlwaysValid());
    }

    public UUIDValue(UUID value, Validator<UUID> validator) {
        this(value, validator, UUID::toString);
    }

    public UUIDValue(UUID value, Validator<UUID> validator, Function<UUID, String> showFn) {
        super(value, validator, showFn);
    }
}
