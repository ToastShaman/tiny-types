package com.github.toastshaman.tinytypes.validation;

import com.github.toastshaman.tinytypes.either.Either;

import java.time.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.lang.String.format;

public interface Validator<T> {

    static <R> Validator<R> of(Predicate<R> predicate, String message) {
        return new TinyTypeValidator<>(predicate, message);
    }

    static <R> Validator<R> of(Predicate<R> predicate, Function<R, String> message) {
        return new TinyTypeValidator<>(predicate, message);
    }

    static Validator<Boolean> IsTrue() {
        return Validator.of(Boolean.TRUE::equals, "must be true");
    }

    static Validator<Boolean> IsFalse() {
        return Validator.of(Boolean.FALSE::equals, "must be false");
    }

    static <R> Validator<R> AlwaysValid() {
        return Validator.of(value -> true, "must be valid");
    }

    static Validator<String> NonBlank() {
        return Validator.of(value -> !value.isBlank(), "must not be blank");
    }

    static Validator<String> NonEmpty() {
        return Validator.of(value -> !value.isEmpty(), "must not be empty");
    }

    static Validator<String> MaxLength(Integer max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value.length() <= max, value -> format("must be less than or equal to %d", max));
    }

    static Validator<String> MinLength(Integer min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value.length() >= min, value -> format("must be greater than or equal to %d", min));
    }

    static Validator<Integer> Min(Integer min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value >= min, value -> format("must be greater than or equal to %d", min));
    }

    static Validator<Long> Min(Long min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value >= min, value -> format("must be greater than or equal to %d", min));
    }

    static Validator<Double> Min(Double min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value >= min, value -> format("must be greater than or equal to %f", min));
    }

    static Validator<Float> Min(Float min) {
        Objects.requireNonNull(min);
        return Validator.of(value -> value >= min, value -> format("must be greater than or equal to %f", min));
    }

    static Validator<Integer> Max(Integer max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value <= max, value -> format("must be less than or equal to %d", max));
    }

    static Validator<Long> Max(Long max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value <= max, value -> format("must be less than or equal to %d", max));
    }

    static Validator<Double> Max(Double max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value <= max, value -> format("must be less than or equal to %f", max));
    }

    static Validator<Float> Max(Float max) {
        Objects.requireNonNull(max);
        return Validator.of(value -> value <= max, value -> format("must be less than or equal to %f", max));
    }

    static Validator<String> Matches(Pattern pattern) {
        Objects.requireNonNull(pattern);
        return Validator.of(value -> pattern.matcher(value).matches(), value -> format("must match %s", pattern));
    }

    static Validator<String> Matches(String regex) {
        return Validator.Matches(Pattern.compile(Objects.requireNonNull(regex)));
    }

    static Validator<Instant> Future(Instant now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now), value -> "must be a future date");
    }

    static Validator<LocalDate> Future(LocalDate now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now), value -> "must be a future date");
    }

    static Validator<LocalDateTime> Future(LocalDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now), value -> "must be a future date");
    }

    static Validator<OffsetTime> Future(OffsetTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now), value -> "must be a future date");
    }

    static Validator<OffsetDateTime> Future(OffsetDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now), value -> "must be a future date");
    }

    static Validator<ZonedDateTime> Future(ZonedDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now), value -> "must be a future date");
    }

    static Validator<Instant> FutureOrPresent(Instant now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now) || value.equals(now), value -> "must be a date in the present or in the future");
    }

    static Validator<LocalDate> FutureOrPresent(LocalDate now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now) || value.equals(now), value -> "must be a date in the present or in the future");
    }

    static Validator<LocalDateTime> FutureOrPresent(LocalDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now) || value.equals(now), value -> "must be a date in the present or in the future");
    }

    static Validator<OffsetTime> FutureOrPresent(OffsetTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now) || value.equals(now), value -> "must be a date in the present or in the future");
    }

    static Validator<OffsetDateTime> FutureOrPresent(OffsetDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now) || value.equals(now), value -> "must be a date in the present or in the future");
    }

    static Validator<ZonedDateTime> FutureOrPresent(ZonedDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isAfter(now) || value.equals(now), value -> "must be a date in the present or in the future");
    }

    static Validator<Instant> Past(Instant now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now), value -> "must be a past date");
    }

    static Validator<LocalDate> Past(LocalDate now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now), value -> "must be a past date");
    }

    static Validator<LocalDateTime> Past(LocalDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now), value -> "must be a past date");
    }

    static Validator<OffsetTime> Past(OffsetTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now), value -> "must be a past date");
    }

    static Validator<OffsetDateTime> Past(OffsetDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now), value -> "must be a past date");
    }

    static Validator<ZonedDateTime> Past(ZonedDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now), value -> "must be a past date");
    }

    static Validator<Instant> PastOrPresent(Instant now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now) || value.equals(now), value -> "must be a date in the past or in the present");
    }

    static Validator<LocalDate> PastOrPresent(LocalDate now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now) || value.equals(now), value -> "must be a date in the past or in the present");
    }

    static Validator<LocalDateTime> PastOrPresent(LocalDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now) || value.equals(now), value -> "must be a date in the past or in the present");
    }

    static Validator<OffsetTime> PastOrPresent(OffsetTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now) || value.equals(now), value -> "must be a date in the past or in the present");
    }

    static Validator<OffsetDateTime> PastOrPresent(OffsetDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now) || value.equals(now), value -> "must be a date in the past or in the present");
    }

    static Validator<ZonedDateTime> PastOrPresent(ZonedDateTime now) {
        Objects.requireNonNull(now);
        return Validator.of(value -> value.isBefore(now) || value.equals(now), value -> "must be a date in the past or in the present");
    }

    default Validator<T> and(Validator<T> other) {
        return new CompositeAndValidator<>(this, other);
    }

    default Validator<T> or(Validator<T> other) {
        return new CompositeOrValidator<>(this, other);
    }

    Either<List<String>, T> isValid(T t);
}
