package com.github.toastshaman.tinytypes.validation;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import io.vavr.control.Either;
import java.time.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
        return Validator.of(
                value -> value.length() >= min, value -> format("must be greater than or equal to %d", min));
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

    static TimeValidatorFactory withClock(Clock clock) {
        return new TimeValidatorFactory(clock);
    }

    class TimeValidatorFactory {
        private final Clock clock;

        public TimeValidatorFactory(Clock clock) {
            this.clock = Objects.requireNonNull(clock);
        }

        public <T> Validator<T> Future() {
            Predicate<T> predicate = it -> {
                if (it instanceof Instant value) return value.isAfter(Instant.now(clock));

                if (it instanceof LocalDate value) return value.isAfter(LocalDate.now(clock));

                if (it instanceof LocalDateTime value) return value.isAfter(LocalDateTime.now(clock));

                if (it instanceof OffsetTime value) return value.isAfter(OffsetTime.now(clock));

                if (it instanceof OffsetDateTime value) return value.isAfter(OffsetDateTime.now(clock));

                if (it instanceof ZonedDateTime value) return value.isAfter(ZonedDateTime.now(clock));

                return false;
            };

            return Validator.of(predicate, value -> "must be a future date");
        }

        public <T> Validator<T> FutureOrPresent() {
            Predicate<T> predicate = it -> {
                if (it instanceof Instant value)
                    return value.isAfter(Instant.now(clock)) || it.equals(Instant.now(clock));

                if (it instanceof LocalDate value)
                    return value.isAfter(LocalDate.now(clock)) || it.equals(LocalDate.now(clock));

                if (it instanceof LocalDateTime value)
                    return value.isAfter(LocalDateTime.now(clock)) || value.equals(LocalDateTime.now(clock));

                if (it instanceof OffsetTime value)
                    return value.isAfter(OffsetTime.now(clock)) || value.equals(OffsetTime.now(clock));

                if (it instanceof OffsetDateTime value)
                    return value.isAfter(OffsetDateTime.now(clock)) || value.equals(OffsetDateTime.now(clock));

                if (it instanceof ZonedDateTime value)
                    return value.isAfter(ZonedDateTime.now(clock)) || value.equals(ZonedDateTime.now(clock));

                return false;
            };

            return Validator.of(predicate, value -> "must be a date in the present or in the future");
        }

        public <T> Validator<T> Past() {
            Predicate<T> predicate = it -> {
                if (it instanceof Instant value) return value.isBefore(Instant.now(clock));

                if (it instanceof LocalDate value) return value.isBefore(LocalDate.now(clock));

                if (it instanceof LocalDateTime value) return value.isBefore(LocalDateTime.now(clock));

                if (it instanceof OffsetTime value) return value.isBefore(OffsetTime.now(clock));

                if (it instanceof OffsetDateTime value) return value.isBefore(OffsetDateTime.now(clock));

                if (it instanceof ZonedDateTime value) return value.isBefore(ZonedDateTime.now(clock));

                return false;
            };

            return Validator.of(predicate, value -> "must be a past date");
        }

        public <T> Validator<T> PastOrPresent() {
            Predicate<T> predicate = it -> {
                if (it instanceof Instant value)
                    return value.isBefore(Instant.now(clock)) || value.equals(Instant.now(clock));

                if (it instanceof LocalDate value)
                    return value.isBefore(LocalDate.now(clock)) || value.equals(LocalDate.now(clock));

                if (it instanceof LocalDateTime value)
                    return value.isBefore(LocalDateTime.now(clock)) || value.equals(LocalDateTime.now(clock));

                if (it instanceof OffsetTime value)
                    return value.isBefore(OffsetTime.now(clock)) || value.equals(OffsetTime.now(clock));

                if (it instanceof OffsetDateTime value)
                    return value.isBefore(OffsetDateTime.now(clock)) || value.equals(OffsetDateTime.now(clock));

                if (it instanceof ZonedDateTime value)
                    return value.isBefore(ZonedDateTime.now(clock)) || value.equals(ZonedDateTime.now(clock));

                return false;
            };

            return Validator.of(predicate, value -> "must be a date in the past or in the present");
        }
    }

    default Validator<T> and(Validator<T> other) {
        return value -> isValid(value).flatMap(other::isValid);
    }

    default Validator<T> or(Validator<T> other) {
        return value -> {
            var first = isValid(value);
            var second = other.isValid(value);

            if (first.isRight() || second.isRight()) {
                return Either.right(value);
            }

            var errors = Stream.of(first, second)
                    .filter(Either::isLeft)
                    .map(Either::getLeft)
                    .flatMap(Collection::stream)
                    .collect(toList());

            return Either.left(errors);
        };
    }

    Either<List<String>, T> isValid(T t);
}
