package com.github.toastshaman.tinytypes.fp;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.Objects;
import java.util.function.Function;

public final class State<S, A> {

    private final Function<S, Tuple2<S, A>> stateF;

    public State(Function<S, Tuple2<S, A>> stateF) {
        this.stateF = Objects.requireNonNull(stateF);
    }

    public static <S, A> State<S, A> of(Function<S, Tuple2<S, A>> stateF) {
        return new State<>(stateF);
    }

    public static <S, A> State<S, A> unit(A value) {
        return new State<>(s -> Tuple.of(s, value));
    }

    public static <S, A> State<S, A> modify(Function<S, S> fn) {
        return new State<>(s -> Tuple.of(fn.apply(s), null));
    }

    public static <S, A> State<S, A> put(S state) {
        return new State<>(s -> Tuple.of(state, null));
    }

    public static <S> State<S, S> get() {
        return new State<>(s -> Tuple.of(s, s));
    }

    public static <S, A> State<S, A> get(Function<S, A> fn) {
        return new State<>(s -> Tuple.of(s, fn.apply(s)));
    }

    public <B> State<S, B> andThen(State<S, B> other) {
        return flatMap(s -> other);
    }

    public <B> State<S, B> flatMap(Function<A, State<S, B>> fn) {
        return new State<>(s -> {
            Tuple2<S, A> result = stateF.apply(s);
            return fn.apply(result._2).stateF.apply(result._1);
        });
    }

    public <B> State<S, B> map(Function<A, B> fn) {
        return flatMap(value -> State.unit(fn.apply(value)));
    }

    public Tuple2<S, A> run(S state) {
        return stateF.apply(state);
    }

    public S execState(S state) {
        return run(state)._1;
    }

    public A evalState(S state) {
        return run(state)._2;
    }
}
