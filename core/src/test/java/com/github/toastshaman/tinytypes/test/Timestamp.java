package com.github.toastshaman.tinytypes.test;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.math.BigInteger;

public class Timestamp extends AbstractValueType<BigInteger> {
    public Timestamp(BigInteger value) {
        super(value, Validator.of(v -> v.signum() == 1, String.format("%s must be positive", value)), BigInteger::toString);
    }
}
