package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.math.BigInteger;
import java.util.function.Function;

public abstract class BigIntegerValue extends AbstractValueType<BigInteger> {

    public BigIntegerValue(BigInteger value) {
        this(value, AlwaysValid());
    }

    public BigIntegerValue(BigInteger value, Validator<BigInteger> validator) {
        this(value, validator, BigInteger::toString);
    }

    public BigIntegerValue(BigInteger value, Validator<BigInteger> validator, Function<BigInteger, String> showFn) {
        super(value, validator, showFn);
    }
}
