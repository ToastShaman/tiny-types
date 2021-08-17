package com.github.toastshaman.tinytypes.fp;

import io.vavr.Function3;
import io.vavr.Function4;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class Lens<A, B> {

    private final Function<A, B> getter;

    private final BiFunction<A, B, A> setter;

    public Lens(Function<A, B> getter, BiFunction<A, B, A> setter) {
        this.getter = Objects.requireNonNull(getter);
        this.setter = Objects.requireNonNull(setter);
    }

    public static <A, B> Lens<A, B> of(Function<A, B> getter, BiFunction<A, B, A> setter) {
        return new Lens<>(getter, setter);
    }

    public static <A, B> Lens<A, B> mutableOf(Function<A, B> getter, BiConsumer<A, B> mutator) {
        Objects.requireNonNull(mutator);
        return new Lens<>(getter, (a, b) -> {
            mutator.accept(a, b);
            return a;
        });
    }

    public B get(A a) {
        return getter.apply(a);
    }

    public Optional<B> maybe(A a) {
        return getter.andThen(Optional::ofNullable).apply(a);
    }

    public A set(A a, B b) {
        return setter.apply(a, b);
    }

    public A mod(A a, UnaryOperator<B> unaryOperator) {
        return set(a, unaryOperator.apply(get(a)));
    }

    public <C> Lens<C, B> compose(Lens<C, A> that) {
        return new Lens<>(c -> get(that.get(c)), (c, b) -> that.mod(c, a -> set(a, b)));
    }

    public <C> Lens<A, C> andThen(Lens<B, C> that) {
        return that.compose(this);
    }

    public <C> Function3<A, B, C, A> zip2(Lens<A, C> second) {
        return (a, b, c) -> second.set(set(a, b), c);
    }

    public <C, D> Function4<A, B, C, D, A> zip3(Lens<A, C> second, Lens<A, D> third) {
        return (a, b, c, d) -> third.set(second.set(set(a, b), c), d);
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
