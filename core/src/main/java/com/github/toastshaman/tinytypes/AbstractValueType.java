package com.github.toastshaman.tinytypes;

import com.github.toastshaman.tinytypes.validation.ValidationException;
import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractValueType<T extends Comparable<? super T>> implements Comparable<AbstractValueType<T>> {

    protected final T value;
    protected final Function<T, String> showFn;
    protected final Validator<T> validator;

    public AbstractValueType(T value, Validator<T> validator, Function<T, String> showFn) {
        this.value = Objects.requireNonNull(value, "value must not be null");
        this.showFn = Objects.requireNonNull(showFn, "showFn must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
        this.validator
                .isValid(value)
                .getOrElseThrow(it -> new ValidationException(this.getClass().getSimpleName(), it));
    }

    public String show() {
        return showFn.apply(value);
    }

    public T unwrap() {
        return value;
    }

    public <U> U transform(Function<T, U> f) {
        return Objects.requireNonNull(f).apply(unwrap());
    }

    public void peek(Consumer<T> f) {
        Objects.requireNonNull(f).accept(value);
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
