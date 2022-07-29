package com.github.toastshaman.tinytypes;

import com.github.toastshaman.tinytypes.functions.TriFunction;
import com.github.toastshaman.tinytypes.validation.ValidationException;
import com.github.toastshaman.tinytypes.validation.Validator;

import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractValueType<T extends Comparable<? super T>> implements Comparable<AbstractValueType<T>> {

    protected final T value;
    protected final Function<T, String> showFn;
    protected final Validator<T> validator;

    public AbstractValueType(T value,
                             Validator<T> validator,
                             Function<T, String> showFn) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(showFn);
        Objects.requireNonNull(validator).isValid(value).peekLeft(it -> {
            throw new ValidationException(this.getClass().getSimpleName(), it);
        });

        this.validator = validator;
        this.showFn = showFn;
        this.value = value;
    }

    public String show() {
        return showFn.apply(value);
    }

    public T unwrap() {
        return value;
    }

    protected <R extends AbstractValueType<T>> R map(
            Function<T, T> mapperFn,
            TriFunction<T, Validator<T>, Function<T, String>, R> constructorFn
    ) {
        return constructorFn.apply(mapperFn.apply(value), validator, showFn);
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

    @Override
    public int compareTo(AbstractValueType<T> other) {
        return value.compareTo(other.value);
    }
}
