package com.github.toastshaman.tinytypes.fp;

import io.vavr.Tuple2;
import java.util.Objects;
import java.util.function.Function;

public final class Converter<S, T> {

    private final Function<S, T> mapper;

    public Converter(Function<S, T> mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    public T convert(S source) {
        return mapper.apply(source);
    }

    public <T2> Converter<S, T2> andThen(Function<T, T2> next) {
        return new Converter<>(mapper.andThen(next));
    }

    public <T2> Converter<S, T2> map(Function<T, T2> next) {
        return new Converter<>(mapper.andThen(next));
    }

    public <T2> Converter<S, T2> andThen(Converter<T, T2> other) {
        return new Converter<>(mapper.andThen(other.mapper));
    }

    public <T2> Converter<S, T2> flatMap(Function<T, Converter<T, T2>> next) {
        return new Converter<>(mapper.andThen(it -> next.apply(it).mapper.apply(it)));
    }

    public <T2> Converter<S, Tuple2<T, T2>> zip(Converter<S, T2> other) {
        return Converter.of(it -> new Tuple2<>(convert(it), other.convert(it)));
    }

    public static <S, T> Converter<S, T> of(Function<S, T> mapper) {
        return new Converter<>(mapper);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Converter<?, ?> converter = (Converter<?, ?>) o;
        return Objects.equals(mapper, converter.mapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapper);
    }
}
