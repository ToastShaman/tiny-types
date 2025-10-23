package com.github.toastshaman.tinytypes.fp;

import java.util.Objects;
import java.util.function.Function;

public record Iso<IN, OUT>(Function<IN, OUT> get, Function<OUT, IN> reverseGet) {

    public Iso {
        Objects.requireNonNull(get);
        Objects.requireNonNull(reverseGet);
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
}
