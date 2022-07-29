package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.math.BigInteger;
import java.util.function.Function;

public class BigIntegerValue extends AbstractValueType<BigInteger> {
    public static BigIntegerValue ZERO = new BigIntegerValue(BigInteger.ZERO);
    public static BigIntegerValue ONE = new BigIntegerValue(BigInteger.ONE);
    public static BigIntegerValue TWO = new BigIntegerValue(BigInteger.TWO);
    public static BigIntegerValue TEN = new BigIntegerValue(BigInteger.TEN);

    public BigIntegerValue(BigInteger value) {
        super(value);
    }

    public BigIntegerValue(BigInteger value, Validator<? super BigInteger> validator) {
        super(value, validator);
    }

    public BigIntegerValue(BigInteger value, Validator<? super BigInteger> validator, Function<BigInteger, String> showFn) {
        super(value, validator, showFn);
    }
}
