package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.math.BigInteger;
import java.util.function.Function;

public class BigIntegerValue extends AbstractValueType<BigInteger> {
    public static BigIntegerValue ZERO = BigIntegerValue.of(BigInteger.ZERO);
    public static BigIntegerValue ONE = BigIntegerValue.of(BigInteger.ONE);
    public static BigIntegerValue TWO = BigIntegerValue.of(BigInteger.TWO);
    public static BigIntegerValue TEN = BigIntegerValue.of(BigInteger.TEN);

    public BigIntegerValue(BigInteger value, Validator<BigInteger> validator, Function<BigInteger, String> showFn) {
        super(value, validator, showFn);
    }

    public static BigIntegerValue of(BigInteger value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static BigIntegerValue of(BigInteger value, Validator<BigInteger> validator) {
        return of(value, validator, Object::toString);
    }

    public static BigIntegerValue of(BigInteger value, Function<BigInteger, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static BigIntegerValue of(BigInteger value,
                                     Validator<BigInteger> validator,
                                     Function<BigInteger, String> show) {
        return new BigIntegerValue(value, validator, show);
    }
}
