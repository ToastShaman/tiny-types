package com.github.toastshaman.tinytypes.fp;

import io.vavr.Tuple2;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class Reader<C, A> {

    private final Function<C, A> reader;

    public Reader(Function<C, A> reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    public A apply(C c) {
        return reader.apply(c);
    }

    public Optional<A> maybe(C c) {
        return reader.andThen(Optional::ofNullable).apply(c);
    }

    public <U> Reader<C, U> map(Function<A, U> f) {
        return new Reader<>(reader.andThen(f));
    }

    public <U> Reader<C, U> flatMap(Function<A, Reader<C, U>> f) {
        return new Reader<>(it -> f.apply(apply(it)).apply(it));
    }

    public <U> Reader<C, Tuple2<A, U>> zip(Reader<C, U> reader) {
        return this.flatMap(a -> reader.map(b -> new Tuple2<>(a, b)));
    }

    public static <C, A> Reader<C, A> of(Function<C, A> f) {
        return new Reader<>(f);
    }

    public static <C, A> Reader<C, A> pure(A a) {
        return new Reader<>(it -> a);
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
