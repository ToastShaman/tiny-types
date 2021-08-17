package com.github.toastshaman.tinytypes.validation;

import io.vavr.control.Either;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class TinyTypeValidator<T> implements Validator<T> {

    public final Predicate<T> predicate;
    public final Function<T, String> message;

    public TinyTypeValidator(Predicate<T> predicate, String message) {
        this(predicate, value -> message);
    }

    public TinyTypeValidator(Predicate<T> predicate, Function<T, String> message) {
        this.predicate = Objects.requireNonNull(predicate);
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public Either<List<String>, T> isValid(T t) {
        return predicate.test(t) ? Either.right(t) : Either.left(List.of(message.apply(t)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TinyTypeValidator<?> that = (TinyTypeValidator<?>) o;
        return predicate.equals(that.predicate) && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, message);
    }
}
