package com.github.toastshaman.tinytypes.validation;

import io.vavr.control.Either;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CompositeOrValidator<T> implements Validator<T> {
    private final Validator<T> first;
    private final Validator<T> second;

    public CompositeOrValidator(Validator<T> first, Validator<T> second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    @Override
    public Either<List<String>, T> isValid(T t) {
        Either<List<String>, T> firstResult = first.isValid(t);
        Either<List<String>, T> secondResult = second.isValid(t);

        if (firstResult.isRight() || secondResult.isRight()) {
            return Either.right(t);
        }

        List<String> errors = Stream.of(firstResult, secondResult)
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .flatMap(Collection::stream)
                .collect(toList());

        return Either.left(errors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeOrValidator<?> that = (CompositeOrValidator<?>) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
