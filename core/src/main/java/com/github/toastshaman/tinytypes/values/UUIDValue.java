package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.UUID;
import java.util.function.Function;

public class UUIDValue extends AbstractValueType<UUID> {

    public UUIDValue(UUID value) {
        super(value);
    }

    public UUIDValue(UUID value, Validator<? super UUID> validator) {
        super(value, validator);
    }

    public UUIDValue(UUID value, Validator<? super UUID> validator, Function<UUID, String> showFn) {
        super(value, validator, showFn);
    }

    public static UUIDValue random() {
        return new UUIDValue(UUID.randomUUID());
    }
}
