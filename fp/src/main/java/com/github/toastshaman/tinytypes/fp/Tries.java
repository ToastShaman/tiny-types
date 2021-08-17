package com.github.toastshaman.tinytypes.fp;

import io.vavr.control.Try;
import java.util.function.BiFunction;

public final class Tries {

    private Tries() {}

    public static <T1, T2, U> Try<U> transform2(Try<T1> first, Try<T2> second, BiFunction<T1, T2, U> mapper) {
        return first.flatMap(a -> second.map(b -> mapper.apply(a, b)));
    }
}
