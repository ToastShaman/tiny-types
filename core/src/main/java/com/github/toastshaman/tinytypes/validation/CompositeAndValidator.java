package com.github.toastshaman.tinytypes.validation;

import io.vavr.control.Either;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CompositeAndValidator<T> implements Validator<T> {
    private final Validator<T> first;
    private final Validator<T> second;

    public CompositeAndValidator(Validator<T> first, Validator<T> second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    @Override
    public Either<List<String>, T> isValid(T t) {
        List<String> errors = Stream.of(first, second)
                .map(it -> it.isValid(t))
                .map(it -> it.swap().getOrElse(List.of()))
                .flatMap(Collection::stream)
                .collect(toList());

        return errors.isEmpty() ? Either.right(t) : Either.left(errors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeAndValidator<?> that = (CompositeAndValidator<?>) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
