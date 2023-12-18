package com.github.toastshaman.tinytypes.fp;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
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
        return new Lens<>(getter, (a, b) -> {
            mutator.accept(a, b);
            return a;
        });
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
