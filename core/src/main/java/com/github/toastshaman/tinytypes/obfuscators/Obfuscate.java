package com.github.toastshaman.tinytypes.obfuscators;

import java.util.function.Function;

public final class Obfuscate {

    private static final String MASK = "*".repeat(8);

    private Obfuscate() {}

    public static <T> Function<T, String> fully() {
        return value -> MASK;
    }

    public static <T> Function<T, String> keepLast(int charsToKeep) {
        return value -> {
            var plain = value.toString();
            if (charsToKeep <= 0 || plain.length() <= charsToKeep) return MASK;
            var keep = plain.substring(plain.length() - charsToKeep);
            return MASK + keep;
        };
    }

    public static <T> Function<T, String> keepFirst(int charsToKeep) {
        return value -> {
            var plain = value.toString();
            if (charsToKeep <= 0 || plain.length() <= charsToKeep) return MASK;
            var keep = plain.substring(0, charsToKeep);
            return keep + MASK;
        };
    }
}
