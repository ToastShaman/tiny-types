package com.github.toastshaman.tinytypes.fp;

import static com.github.toastshaman.tinytypes.fp.Result.Failure;
import static com.github.toastshaman.tinytypes.fp.Result.Success;
import static java.util.Objects.requireNonNull;

import io.vavr.CheckedFunction0;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Function5;
import io.vavr.Function6;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed interface Result<T, E> permits Success, Failure {

    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    static <T, E> Result<T, E> failure(E value) {
        return new Failure<>(value);
    }

    static <T> Result<T, Throwable> of(CheckedFunction0<? extends T> supplier) {
        try {
            return new Success<>(supplier.apply());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    static <T> Result<T, Throwable> ofSupplier(Supplier<? extends T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable e) {
            return new Failure<>(e);
        }
    }

    static <T> Result<T, Throwable> ofCallable(Callable<? extends T> supplier) {
        try {
            return new Success<>(supplier.call());
        } catch (Throwable e) {
            return new Failure<>(e);
        }
    }

    static <T> Result<T, Throwable> ofOptional(Supplier<Optional<? extends T>> supplier) {
        try {
            return supplier.get()
                    .map(Result::<T, Throwable>success)
                    .orElseGet(() -> Result.failure(new NoSuchElementException()));
        } catch (Throwable e) {
            return new Failure<>(e);
        }
    }

    record PartitionedResult<T, E>(List<T> successes, List<E> failures) {
        public PartitionedResult {
            requireNonNull(successes);
            requireNonNull(failures);
        }

        boolean hasFailures() {
            return !failures.isEmpty();
        }
    }

    @SafeVarargs
    static <T, E> PartitionedResult<T, E> partition(Result<T, E>... values) {
        return partition(Arrays.stream(values).toList());
    }

    static <T, E> PartitionedResult<T, E> partition(Iterable<Result<T, E>> values) {
        var oks = new ArrayList<T>();
        var errs = new ArrayList<E>();

        for (Result<T, E> result : values) {
            if (result instanceof Result.Success<T, E> success) oks.add(success.value);
            if (result instanceof Result.Failure<T, E> failure) errs.add(failure.reason);
        }

        return new PartitionedResult<>(List.copyOf(oks), List.copyOf(errs));
    }

    static <T, E> List<T> anyValues(Iterable<Result<T, E>> values) {
        return StreamSupport.stream(values.spliterator(), false)
                .map(Result::getOrNull)
                .filter(Objects::nonNull)
                .toList();
    }

    static <T, E> Result<List<T>, E> allValues(Iterable<Result<T, E>> values) {
        var partition = partition(values);
        var ok = partition.successes;
        var errs = partition.failures;
        return errs.isEmpty() ? new Success<>(ok) : new Failure<>(errs.getFirst());
    }

    static <T, T1, E> Result<T1, E> foldResult(
            Iterable<T> values, Result<T1, E> initial, Function2<T1, T, Result<T1, E>> operation) {
        var accumulator = initial;
        for (T el : values) {
            accumulator = accumulator.flatMap(value -> operation.apply(value, el));
        }
        return accumulator;
    }

    static <T, T1, E> Result<List<T1>, E> mapAllValues(Iterable<T> values, Function<T, Result<T1, E>> f) {
        var acc = new ArrayList<T1>();

        for (T value : values) {
            var result = f.apply(value);

            if (result instanceof Result.Success<T1, E> success) {
                acc.add(success.value);
            }

            if (result instanceof Result.Failure<T1, E> failure) {
                return Result.failure(failure.reason);
            }
        }

        return Result.success(List.copyOf(acc));
    }

    static <T1, T2, U, E> Result<U, E> zip(Result<T1, E> r1, Result<T2, E> r2, Function2<T1, T2, U> fn) {
        return r1.flatMap(v1 -> r2.map(v2 -> fn.apply(v1, v2)));
    }

    static <T1, T2, T3, U, E> Result<U, E> zip(
            Result<T1, E> r1, Result<T2, E> r2, Result<T3, E> r3, Function3<T1, T2, T3, U> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.map(v3 -> fn.apply(v1, v2, v3))));
    }

    static <T1, T2, T3, T4, U, E> Result<U, E> zip(
            Result<T1, E> r1, Result<T2, E> r2, Result<T3, E> r3, Result<T4, E> r4, Function4<T1, T2, T3, T4, U> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> r4.map(v4 -> fn.apply(v1, v2, v3, v4)))));
    }

    static <T1, T2, T3, T4, T5, U, E> Result<U, E> zip(
            Result<T1, E> r1,
            Result<T2, E> r2,
            Result<T3, E> r3,
            Result<T4, E> r4,
            Result<T5, E> r5,
            Function5<T1, T2, T3, T4, T5, U> fn) {
        return r1.flatMap(
                v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> r4.flatMap(v4 -> r5.map(v5 -> fn.apply(v1, v2, v3, v4, v5))))));
    }

    static <T1, T2, T3, T4, T5, T6, U, E> Result<U, E> zip(
            Result<T1, E> r1,
            Result<T2, E> r2,
            Result<T3, E> r3,
            Result<T4, E> r4,
            Result<T5, E> r5,
            Result<T6, E> r6,
            Function6<T1, T2, T3, T4, T5, T6, U> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 ->
                r3.flatMap(v3 -> r4.flatMap(v4 -> r5.flatMap(v5 -> r6.map(v6 -> fn.apply(v1, v2, v3, v4, v5, v6)))))));
    }

    static <T1, T2, U, E> Result<U, E> flatZip(Result<T1, E> r1, Result<T2, E> r2, Function2<T1, T2, Result<U, E>> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> fn.apply(v1, v2)));
    }

    static <T1, T2, T3, U, E> Result<U, E> flatZip(
            Result<T1, E> r1, Result<T2, E> r2, Result<T3, E> r3, Function3<T1, T2, T3, Result<U, E>> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> fn.apply(v1, v2, v3))));
    }

    static <T1, T2, T3, T4, U, E> Result<U, E> flatZip(
            Result<T1, E> r1,
            Result<T2, E> r2,
            Result<T3, E> r3,
            Result<T4, E> r4,
            Function4<T1, T2, T3, T4, Result<U, E>> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> r4.flatMap(v4 -> fn.apply(v1, v2, v3, v4)))));
    }

    static <T1, T2, T3, T4, T5, U, E> Result<U, E> flatZip(
            Result<T1, E> r1,
            Result<T2, E> r2,
            Result<T3, E> r3,
            Result<T4, E> r4,
            Result<T5, E> r5,
            Function5<T1, T2, T3, T4, T5, Result<U, E>> fn) {
        return r1.flatMap(v1 ->
                r2.flatMap(v2 -> r3.flatMap(v3 -> r4.flatMap(v4 -> r5.flatMap(v5 -> fn.apply(v1, v2, v3, v4, v5))))));
    }

    static <T1, T2, T3, T4, T5, T6, U, E> Result<U, E> flatZip(
            Result<T1, E> r1,
            Result<T2, E> r2,
            Result<T3, E> r3,
            Result<T4, E> r4,
            Result<T5, E> r5,
            Result<T6, E> r6,
            Function6<T1, T2, T3, T4, T5, T6, Result<U, E>> fn) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(
                v3 -> r4.flatMap(v4 -> r5.flatMap(v5 -> r6.flatMap(v6 -> fn.apply(v1, v2, v3, v4, v5, v6)))))));
    }

    boolean isSuccess();

    boolean isFailure();

    <T1> Result<T1, E> flatMap(Function<T, Result<T1, E>> fn);

    <T1> Result<T1, E> map(Function<T, T1> fn);

    <T1, E1> Result<T1, E1> bimap(Function<T, T1> fn1, Function<E, E1> fn2);

    <E1> Result<T, E1> mapFailure(Function<E, E1> fn);

    <E1> Result<T, E1> flatMapFailure(Function<E, Result<T, E1>> fn);

    Result<T, E> filter(Predicate<T> predicate, Function<T, E> errorProvider);

    Result<T, E> onSuccess(Consumer<T> fn);

    Result<T, E> onFailure(Consumer<E> fn);

    T getOrElse(T other);

    T getOrElseGet(Supplier<T> other);

    T getOrNull();

    Result<T, E> or(Supplier<Result<T, E>> f);

    <X extends Throwable> T getOrThrow(Function<E, X> f) throws X;

    Result<E, T> swap();

    <R> R fold(Function<T, R> successFn, Function<E, R> failureFn);

    T recover(Function<E, T> f);

    Optional<T> maybe();

    Either<E, T> asEither();

    <X extends Throwable> Try<T> asTry(Function<E, X> f);

    Stream<T> stream();

    record Success<T, E>(T value) implements Result<T, E> {

        public Success {
            Objects.requireNonNull(value, "value must not be null");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public <T1> Result<T1, E> flatMap(Function<T, Result<T1, E>> fn) {
            return fn.apply(value);
        }

        @Override
        public <T1> Result<T1, E> map(Function<T, T1> fn) {
            return flatMap(value -> new Success<>(fn.apply(value)));
        }

        @Override
        public <T1, E1> Result<T1, E1> bimap(Function<T, T1> fn1, Function<E, E1> fn2) {
            return map(fn1).mapFailure(fn2);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E1> Result<T, E1> mapFailure(Function<E, E1> fn) {
            return (Result<T, E1>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E1> Result<T, E1> flatMapFailure(Function<E, Result<T, E1>> fn) {
            return (Result<T, E1>) this;
        }

        @Override
        public Result<T, E> filter(Predicate<T> predicate, Function<T, E> errorProvider) {
            if (predicate.test(value)) {
                return this;
            }
            return new Failure<>(errorProvider.apply(value));
        }

        @Override
        public Result<T, E> onSuccess(Consumer<T> fn) {
            fn.accept(value);
            return this;
        }

        @Override
        public Result<T, E> onFailure(Consumer<E> fn) {
            return this;
        }

        @Override
        public T getOrElse(T other) {
            return value;
        }

        @Override
        public T getOrElseGet(Supplier<T> other) {
            return value;
        }

        @Override
        public T getOrNull() {
            return value;
        }

        @Override
        public Result<T, E> or(Supplier<Result<T, E>> f) {
            return this;
        }

        @Override
        public <X extends Throwable> T getOrThrow(Function<E, X> f) {
            return value;
        }

        @Override
        public Result<E, T> swap() {
            return new Failure<>(value);
        }

        @Override
        public <R> R fold(Function<T, R> successFn, Function<E, R> failureFn) {
            return successFn.apply(value);
        }

        @Override
        public T recover(Function<E, T> f) {
            return value;
        }

        @Override
        public Optional<T> maybe() {
            return Optional.of(value);
        }

        @Override
        public Either<E, T> asEither() {
            return Either.right(value);
        }

        @Override
        public <X extends Throwable> Try<T> asTry(Function<E, X> f) {
            return Try.success(value);
        }

        @Override
        public Stream<T> stream() {
            return Stream.of(value);
        }
    }

    record Failure<T, E>(E reason) implements Result<T, E> {

        public Failure {
            Objects.requireNonNull(reason, "reason must not be null");
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T1> Result<T1, E> flatMap(Function<T, Result<T1, E>> fn) {
            return (Result<T1, E>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T1> Result<T1, E> map(Function<T, T1> fn) {
            return (Result<T1, E>) this;
        }

        @Override
        public <T1, E1> Result<T1, E1> bimap(Function<T, T1> fn1, Function<E, E1> fn2) {
            return map(fn1).mapFailure(fn2);
        }

        @Override
        public <E1> Result<T, E1> mapFailure(Function<E, E1> fn) {
            return flatMapFailure(value -> new Failure<>(fn.apply(value)));
        }

        @Override
        public <E1> Result<T, E1> flatMapFailure(Function<E, Result<T, E1>> fn) {
            return fn.apply(reason);
        }

        @Override
        public Result<T, E> filter(Predicate<T> predicate, Function<T, E> errorProvider) {
            return this;
        }

        @Override
        public Result<T, E> onSuccess(Consumer<T> fn) {
            return this;
        }

        @Override
        public Result<T, E> onFailure(Consumer<E> fn) {
            fn.accept(reason);
            return this;
        }

        @Override
        public T getOrElse(T other) {
            return other;
        }

        @Override
        public T getOrElseGet(Supplier<T> other) {
            return other.get();
        }

        @Override
        public T getOrNull() {
            return null;
        }

        @Override
        public Result<T, E> or(Supplier<Result<T, E>> f) {
            return f.get();
        }

        @Override
        public <X extends Throwable> T getOrThrow(Function<E, X> f) throws X {
            throw f.apply(reason);
        }

        @Override
        public Result<E, T> swap() {
            return new Success<>(reason);
        }

        @Override
        public <R> R fold(Function<T, R> successFn, Function<E, R> failureFn) {
            return failureFn.apply(reason);
        }

        @Override
        public T recover(Function<E, T> f) {
            return f.apply(reason);
        }

        @Override
        public Optional<T> maybe() {
            return Optional.empty();
        }

        @Override
        public Either<E, T> asEither() {
            return Either.left(reason);
        }

        @Override
        public <X extends Throwable> Try<T> asTry(Function<E, X> f) {
            return Try.failure(f.apply(reason));
        }

        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }
    }
}
