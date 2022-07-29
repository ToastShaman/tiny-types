package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

public class BigDecimalValue extends AbstractValueType<BigDecimal> {

    public static BigDecimalValue ZERO = new BigDecimalValue(BigDecimal.ZERO);
    public static BigDecimalValue ONE = new BigDecimalValue(BigDecimal.ONE);
    public static BigDecimalValue TEN = new BigDecimalValue(BigDecimal.TEN);

    public BigDecimalValue(BigDecimal value) {
        super(value);
    }

    public BigDecimalValue(BigDecimal value, Validator<? super BigDecimal> validator) {
        super(value, validator);
    }

    public BigDecimalValue(BigDecimal value, Function<BigDecimal, String> showFn) {
        super(value, showFn);
    }

    public BigDecimalValue(BigDecimal value, Validator<? super BigDecimal> validator, Function<BigDecimal, String> showFn) {
        super(value, validator, showFn);
    }
}
