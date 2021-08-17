package com.github.toastshaman.tinytypes.fp;

import java.util.Objects;
import java.util.function.Function;

/*
 * @param S the source of a [PIso]
 * @param T the modified source of a [PIso]
 * @param A the focus of a [PIso]
 * @param B the modified target of a [PIso]
 */
public final class PIso<S, T, A, B> {

    private final Function<S, A> get;
    private final Function<B, T> reverseGet;

    public PIso(Function<S, A> get, Function<B, T> reverseGet) {
        this.get = Objects.requireNonNull(get);
        this.reverseGet = Objects.requireNonNull(reverseGet);
    }

    public static <S, T, A, B> PIso<S, T, A, B> of(Function<S, A> get, Function<B, T> reverseGet) {
        return new PIso<>(get, reverseGet);
    }

    public A get(S source) {
        return get.apply(source);
    }

    public T reverseGet(B focus) {
        return reverseGet.apply(focus);
    }

    public T modify(S source, Function<A, B> map) {
        return reverseGet(map.apply(get(source)));
    }

    public T set(B b) {
        return reverseGet(b);
    }

    public PIso<B, A, T, S> reverse() {
        return new PIso<>(this::reverseGet, this::get);
    }

    public <C, D> PIso<S, T, C, D> compose(PIso<A, B, C, D> other) {
        return new PIso<>(other.get.compose(this.get), this.reverseGet.compose(other.reverseGet));
    }

    public <C, D> PIso<S, T, C, D> plus(PIso<A, B, C, D> other) {
        return this.compose(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PIso<?, ?, ?, ?> pIso = (PIso<?, ?, ?, ?>) o;
        return Objects.equals(get, pIso.get) && Objects.equals(reverseGet, pIso.reverseGet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(get, reverseGet);
    }
}
