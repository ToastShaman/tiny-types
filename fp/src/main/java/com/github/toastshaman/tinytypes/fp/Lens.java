package com.github.toastshaman.tinytypes.fp;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class Lens<S, A> {

    private final Function<S, A> getter;

    private final BiFunction<S, A, S> setter;

    public Lens(Function<S, A> getter, BiFunction<S, A, S> setter) {
        this.getter = Objects.requireNonNull(getter);
        this.setter = Objects.requireNonNull(setter);
    }

    public static <S, A> Lens<S, A> of(Function<S, A> getter, BiFunction<S, A, S> setter) {
        return new Lens<>(getter, setter);
    }

    public static <S, A> Lens<S, A> mutableOf(Function<S, A> getter, BiConsumer<S, A> mutator) {
        Objects.requireNonNull(mutator);
        return Lens.of(getter, (a, b) -> {
            mutator.accept(a, b);
            return a;
        });
    }

    public static <S, A, B> Lens<S, Tuple2<A, B>> fold(Lens<S, A> l1, Lens<S, B> l2) {
        return Lens.of(s -> Tuple.of(l1.get(s), l2.get(s)), (s, t) -> l2.set(l1.set(s, t._1), t._2));
    }

    public static <S, A, B, C> Lens<S, Tuple3<A, B, C>> fold(Lens<S, A> l1, Lens<S, B> l2, Lens<S, C> l3) {
        return Lens.of(
                s -> Tuple.of(l1.get(s), l2.get(s), l3.get(s)), (s, t) -> l3.set(l2.set(l1.set(s, t._1), t._2), t._3));
    }

    public static <S, A, B, C, D> Lens<S, Tuple4<A, B, C, D>> fold(
            Lens<S, A> l1, Lens<S, B> l2, Lens<S, C> l3, Lens<S, D> l4) {
        return Lens.of(
                s -> Tuple.of(l1.get(s), l2.get(s), l3.get(s), l4.get(s)),
                (s, t) -> l4.set(l3.set(l2.set(l1.set(s, t._1), t._2), t._3), t._4));
    }

    public A get(S s) {
        return getter.apply(s);
    }

    public Optional<A> maybe(S s) {
        return getter.andThen(Optional::ofNullable).apply(s);
    }

    public S set(S s, A a) {
        return setter.apply(s, a);
    }

    public S mod(S s, UnaryOperator<A> f) {
        return set(s, f.apply(get(s)));
    }

    public <C> Lens<C, A> compose(Lens<C, S> before) {
        return new Lens<>(c -> get(before.get(c)), (c, a) -> before.mod(c, s -> set(s, a)));
    }

    public <C> Lens<S, C> andThen(Lens<A, C> after) {
        return after.compose(this);
    }

    public <B> Reader<S, B> map(Function<A, B> f) {
        return Reader.of(getter.andThen(f));
    }

    public Reader<S, Optional<A>> filter(Predicate<A> f) {
        return map(a -> Optional.ofNullable(a).filter(f));
    }

    public Reader<S, A> asReader() {
        return Reader.of(getter);
    }

    public Result<A, Throwable> asResult(S s) {
        return Result.of(() -> getter.apply(s));
    }

    public Option<A> asOption(S s) {
        return Option.ofOptional(maybe(s));
    }

    public Try<A> asTry(S s) {
        return asOption(s).toTry();
    }

    public Either<Throwable, A> asEither(S s) {
        return asTry(s).toEither();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lens<?, ?> lens = (Lens<?, ?>) o;
        return Objects.equals(getter, lens.getter) && Objects.equals(setter, lens.setter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getter, setter);
    }
}
