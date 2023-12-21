package com.github.toastshaman.tinytypes.fp;

import java.util.Objects;
import java.util.function.Function;

public final class Iso<IN, OUT> {

    private final Function<IN, OUT> get;
    private final Function<OUT, IN> reverseGet;

    public Iso(Function<IN, OUT> get, Function<OUT, IN> reverseGet) {
        this.get = Objects.requireNonNull(get);
        this.reverseGet = Objects.requireNonNull(reverseGet);
    }

    public static <IN, OUT> Iso<IN, OUT> of(Function<IN, OUT> get, Function<OUT, IN> reverseGet) {
        return new Iso<>(get, reverseGet);
    }

    public OUT get(IN in) {
        return get.apply(in);
    }

    public IN reverseGet(OUT out) {
        return reverseGet.apply(out);
    }

    public Reader<IN, OUT> asReader() {
        return Reader.of(get);
    }

    public Reader<OUT, IN> asReverseReader() {
        return Reader.of(reverseGet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Iso<?, ?> bidiLens = (Iso<?, ?>) o;
        return Objects.equals(get, bidiLens.get) && Objects.equals(reverseGet, bidiLens.reverseGet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(get, reverseGet);
    }
}
