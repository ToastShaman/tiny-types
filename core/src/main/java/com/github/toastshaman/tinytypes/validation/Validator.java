package com.github.toastshaman.tinytypes.validation;

import io.vavr.control.Either;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface Validator<T> {

    static <R> Validator<R> of(Predicate<R> predicate, String message) {
        return new TinyTypeValidator<>(predicate, message);
    }

    static <R> Validator<R> of(Predicate<R> predicate, Function<R, String> message) {
        return new TinyTypeValidator<>(predicate, message);
    }

    static Validator<Object> AlwaysValid() {
        return Validator.of(value -> true, "is valid");
    }

    static Validator<String> NonBlank() {
        return Validator.of(value -> !value.isBlank(), "value is blank");
    }

    static Validator<String> NonEmpty() {
        return Validator.of(value -> !value.isEmpty(), "value is empty");
    }

    static Validator<String> MaxLength(Integer max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value.length() < max, value -> String.format("length of %d exceeds max length of %d", value.length(), max));
    }

    static Validator<String> MinLength(Integer min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value.length() > min, value -> String.format("length of %d must be greater than %d", value.length(), min));
    }

    static Validator<Integer> Min(Integer min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value >= min, value -> String.format("%d must be greater than %d", value, min));
    }

    static Validator<Integer> Max(Integer max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value <= max, value -> String.format("%d must be smaller than %d", value, max));
    }

    static Validator<String> Matches(Pattern pattern) {
        Objects.requireNonNull(pattern);
        return Validator.of(value -> pattern.matcher(value).matches(), value -> String.format("%s must match %s", value, pattern));
    }

    static Validator<String> Matches(String regex) {
        Objects.requireNonNull(regex);
        return Validator.of(value -> Pattern.compile(regex).matcher(value).matches(), value -> String.format("%s must match %s", value, regex));
    }

    default Validator<T> and(Validator<T> other) {
        return new CompositeTinyTypeValidator<>(this, other);
    }

    Either<List<String>, T> isValid(T t);
}
