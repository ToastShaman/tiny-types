package com.github.toastshaman.tinytypes;

import com.github.toastshaman.tinytypes.validation.ValidationException;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.Objects;
import java.util.function.Function;

public class AbstractValueType<T> {
    protected final T value;
    private final Function<T, String> showFn;

    public AbstractValueType(T value) {
        this(value, Validator.AlwaysValid(), Object::toString);
    }

    public AbstractValueType(T value, Validator<? super T> validator) {
        this(value, validator, Object::toString);
    }

    public AbstractValueType(T value, Validator<? super T> validator, Function<T, String> showFn) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(showFn);
        Objects.requireNonNull(validator).isValid(value).peekLeft(it -> {
            throw new ValidationException(this.getClass().getSimpleName(), it);
        });

        this.showFn = showFn;
        this.value = value;
    }

    public String show() {
        return showFn.apply(value);
    }

    public T unwrap() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractValueType<?> that = (AbstractValueType<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return show();
    }
}
