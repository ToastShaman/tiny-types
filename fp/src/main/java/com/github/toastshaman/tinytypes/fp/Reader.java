package com.github.toastshaman.tinytypes.fp;

import io.vavr.*;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class Reader<S, A> {

    private final Function<S, A> reader;

    public Reader(Function<S, A> reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    public A apply(S s) {
        return reader.apply(s);
    }

    public Optional<A> maybe(S s) {
        return reader.andThen(Optional::ofNullable).apply(s);
    }

    public <U> Reader<S, U> map(Function<A, U> f) {
        return new Reader<>(reader.andThen(f));
    }

    public <U> Reader<S, U> flatMap(Function<A, Reader<S, U>> f) {
        return new Reader<>(s -> f.apply(apply(s)).apply(s));
    }

    public static <S, A> Reader<S, A> of(Function<S, A> f) {
        return new Reader<>(f);
    }

    public static <S, A> Reader<S, A> pure(A a) {
        return new Reader<>(s -> a);
    }

    public static <S, A, B> Reader<S, Tuple2<A, B>> fold(Reader<S, A> r1, Reader<S, B> r2) {
        return new Reader<>(s -> Tuple.of(r1.apply(s), r2.apply(s)));
    }

    public static <S, A, B, C> Reader<S, Tuple3<A, B, C>> fold(Reader<S, A> r1, Reader<S, B> r2, Reader<S, C> r3) {
        return new Reader<>(s -> Tuple.of(r1.apply(s), r2.apply(s), r3.apply(s)));
    }

    public static <S, A, B, C, D> Reader<S, Tuple4<A, B, C, D>> fold(
            Reader<S, A> r1, Reader<S, B> r2, Reader<S, C> r3, Reader<S, D> r4) {
        return new Reader<>(s -> Tuple.of(r1.apply(s), r2.apply(s), r3.apply(s), r4.apply(s)));
    }

    public static <S, A, B, U> Reader<S, U> zip(Reader<S, A> r1, Reader<S, B> r2, Function2<A, B, U> f) {
        return r1.flatMap(v1 -> r2.map(v2 -> f.apply(v1, v2)));
    }

    public static <S, A, B, C, U> Reader<S, U> zip(
            Reader<S, A> r1, Reader<S, B> r2, Reader<S, C> r3, Function3<A, B, C, U> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.map(v3 -> f.apply(v1, v2, v3))));
    }

    public static <S, A, B, C, D, U> Reader<S, U> zip(
            Reader<S, A> r1, Reader<S, B> r2, Reader<S, C> r3, Reader<S, D> r4, Function4<A, B, C, D, U> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> r4.map(v4 -> f.apply(v1, v2, v3, v4)))));
    }

    public static <S, A, B, U> Reader<S, U> flatZip(Reader<S, A> r1, Reader<S, B> r2, Function2<A, B, Reader<S, U>> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> f.apply(v1, v2)));
    }

    public static <S, A, B, C, U> Reader<S, U> flatZip(
            Reader<S, A> r1, Reader<S, B> r2, Reader<S, C> r3, Function3<A, B, C, Reader<S, U>> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> f.apply(v1, v2, v3))));
    }

    public static <S, A, B, C, D, U> Reader<S, U> flatZip(
            Reader<S, A> r1, Reader<S, B> r2, Reader<S, C> r3, Reader<S, D> r4, Function4<A, B, C, D, Reader<S, U>> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> r3.flatMap(v3 -> r4.flatMap(v4 -> f.apply(v1, v2, v3, v4)))));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reader<?, ?> reader1 = (Reader<?, ?>) o;
        return Objects.equals(reader, reader1.reader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reader);
    }
}
