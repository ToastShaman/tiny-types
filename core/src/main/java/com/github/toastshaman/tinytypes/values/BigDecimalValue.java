package com.github.toastshaman.tinytypes.values;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;

import com.github.toastshaman.tinytypes.AbstractValueType;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.math.BigDecimal;
import java.util.function.Function;

public abstract class BigDecimalValue extends AbstractValueType<BigDecimal> {

    public BigDecimalValue(BigDecimal value) {
        this(value, AlwaysValid());
    }

    public BigDecimalValue(BigDecimal value, Validator<BigDecimal> validator) {
        this(value, validator, BigDecimal::toPlainString);
    }

    public BigDecimalValue(BigDecimal value, Validator<BigDecimal> validator, Function<BigDecimal, String> showFn) {
        super(value, validator, showFn);
    }
}
