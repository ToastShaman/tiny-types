package com.github.toastshaman.tinytypes.fp;

import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Suppliers {

    private Suppliers() {}

    public static <T, R> Supplier<R> compose(Function<T, R> f, Supplier<T> s) {
        return () -> f.apply(s.get());
    }

    public static <S1, S2, R> R zip(Supplier<S1> s1, Supplier<S2> s2, Function2<S1, S2, R> f) {
        return f.apply(s1.get(), s2.get());
    }

    public static <S1, S2, S3, R> R zip(Supplier<S1> s1, Supplier<S2> s2, Supplier<S3> s3, Function3<S1, S2, S3, R> f) {
        return f.apply(s1.get(), s2.get(), s3.get());
    }

    public static <S1, S2, S3, S4, R> R zip(
            Supplier<S1> s1, Supplier<S2> s2, Supplier<S3> s3, Supplier<S4> s4, Function4<S1, S2, S3, S4, R> f) {
        return f.apply(s1.get(), s2.get(), s3.get(), s4.get());
    }
}
