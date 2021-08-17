package com.github.toastshaman.tinytypes.validation;

import io.vavr.control.Either;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class CompositeTinyTypeValidator<T> implements Validator<T> {
    private final List<Validator<T>> validators;

    public CompositeTinyTypeValidator(Validator<T> first, Validator<T> second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        this.validators = List.of(first, second);
    }

    public Either<List<String>, T> isValid(T t) {
        List<String> errors = validators.stream()
                .map(it -> it.isValid(t))
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .flatMap(List::stream)
                .collect(toList());
        return errors.isEmpty() ? Either.right(t) : Either.left(errors);
    }
}
