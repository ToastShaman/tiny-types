package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.math.BigDecimal;
import java.util.function.Function;

public class BigDecimalValue extends AbstractValueType<BigDecimal> {
    public static BigDecimalValue ZERO = BigDecimalValue.of(BigDecimal.ZERO);
    public static BigDecimalValue ONE = BigDecimalValue.of(BigDecimal.ONE);
    public static BigDecimalValue TEN = BigDecimalValue.of(BigDecimal.TEN);

    public BigDecimalValue(BigDecimal value, Validator<BigDecimal> validator, Function<BigDecimal, String> showFn) {
        super(value, validator, showFn);
    }

    public BigDecimalValue map(Function<BigDecimal, BigDecimal> mapperFn) {
        return map(mapperFn, BigDecimalValue::new);
    }

    public static BigDecimalValue of(BigDecimal value) {
        return of(value, Validator.AlwaysValid(), Object::toString);
    }

    public static BigDecimalValue of(BigDecimal value, Validator<BigDecimal> validator) {
        return of(value, validator, Object::toString);
    }

    public static BigDecimalValue of(BigDecimal value, Function<BigDecimal, String> showFn) {
        return of(value, Validator.AlwaysValid(), showFn);
    }

    public static BigDecimalValue of(BigDecimal value,
                                     Validator<BigDecimal> validator,
                                     Function<BigDecimal, String> show) {
        return new BigDecimalValue(value, validator, show);
    }
}
