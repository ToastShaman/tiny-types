package com.github.toastshaman.tinytypes.fp;

import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public final class Tries {

    private Tries() {}

    public static <T1, T2, U> Try<U> zip(Try<T1> t1, Try<T2> t2, Function2<T1, T2, U> fn) {
        return t1.flatMap(a -> t2.map(b -> fn.apply(a, b)));
    }

    public static <T1, T2, T3, U> Try<U> zip(Try<T1> t1, Try<T2> t2, Try<T3> t3, Function3<T1, T2, T3, U> fn) {
        return t1.flatMap(a -> t2.flatMap(b -> t3.map(c -> fn.apply(a, b, c))));
    }

    public static <T1, T2, T3, T4, U> Try<U> zip(
            Try<T1> t1, Try<T2> t2, Try<T3> t3, Try<T4> t4, Function4<T1, T2, T3, T4, U> fn) {
        return t1.flatMap(a -> t2.flatMap(b -> t3.flatMap(c -> t4.map(d -> fn.apply(a, b, c, d)))));
    }

    public static <T1, T2, U> Try<U> flatZip(Try<T1> t1, Try<T2> t2, Function2<T1, T2, Try<U>> fn) {
        return t1.flatMap(a -> t2.flatMap(b -> fn.apply(a, b)));
    }

    public static <T1, T2, T3, U> Try<U> flatZip(Try<T1> t1, Try<T2> t2, Try<T3> t3, Function3<T1, T2, T3, Try<U>> fn) {
        return t1.flatMap(a -> t2.flatMap(b -> t3.flatMap(c -> fn.apply(a, b, c))));
    }

    public static <T1, T2, T3, T4, U> Try<U> flatZip(
            Try<T1> t1, Try<T2> t2, Try<T3> t3, Try<T4> t4, Function4<T1, T2, T3, T4, Try<U>> fn) {
        return t1.flatMap(a -> t2.flatMap(b -> t3.flatMap(c -> t4.flatMap(d -> fn.apply(a, b, c, d)))));
    }

    public static <T> Tuple2<List<T>, List<Throwable>> partition(Iterable<Try<T>> values) {
        var oks = new ArrayList<T>();
        var errs = new ArrayList<Throwable>();

        for (Try<T> value : values) {
            if (value.isSuccess()) oks.add(value.get());
            if (value.isFailure()) errs.add(value.getCause());
        }

        return Tuple.of(List.copyOf(oks), List.copyOf(errs));
    }

    public static <T> List<T> anyValues(Iterable<Try<T>> values) {
        return StreamSupport.stream(values.spliterator(), false)
                .filter(Try::isSuccess)
                .map(Try::get)
                .toList();
    }

    public static <T> Try<List<T>> allValues(Iterable<Try<T>> values) {
        var partition = partition(values);
        var ok = partition._1;
        var errs = partition._2;

        return errs.isEmpty() ? Try.success(ok) : Try.failure(errs.getFirst());
    }

    public static <T, T1> Try<T1> foldResult(Iterable<T> values, Try<T1> initial, Function2<T1, T, Try<T1>> operation) {
        var accumulator = initial;
        for (T el : values) {
            accumulator = accumulator.flatMap(value -> operation.apply(value, el));
        }
        return accumulator;
    }

    public static <T, T1> Try<List<T1>> mapAllValues(Iterable<T> values, Function<T, Try<T1>> f) {
        var acc = new ArrayList<T1>();

        for (T value : values) {
            var result = f.apply(value);

            if (result.isSuccess()) {
                acc.add(result.get());
            }

            if (result.isFailure()) {
                return Try.failure(result.getCause());
            }
        }

        return Try.success(List.copyOf(acc));
    }

    public static <T> Result<T, Throwable> toResult(Try<T> t) {
        return t.isSuccess() ? Result.success(t.get()) : Result.failure(t.getCause());
    }
}
