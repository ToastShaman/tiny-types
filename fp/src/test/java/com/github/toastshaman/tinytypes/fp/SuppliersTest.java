package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class SuppliersTest {

    @Test
    void compose_applies_function_after_supplier() {
        Supplier<String> source = () -> "hello";
        Function<String, Integer> mapper = String::length;
        Supplier<Integer> composed = Suppliers.compose(mapper, source);

        assertThat(composed.get()).isEqualTo(5);
    }

    @Test
    void zip_two_suppliers_combines() {
        Supplier<Integer> a = () -> 2;
        Supplier<Integer> b = () -> 3;
        Function2<Integer, Integer, Integer> sum = Integer::sum;

        int result = Suppliers.zip(a, b, sum);
        assertThat(result).isEqualTo(5);
    }

    @Test
    void zip_three_suppliers_combines() {
        Supplier<Integer> a = () -> 1;
        Supplier<Integer> b = () -> 2;
        Supplier<Integer> c = () -> 3;
        Function3<Integer, Integer, Integer, Integer> sum3 = (x, y, z) -> x + y + z;

        int result = Suppliers.zip(a, b, c, sum3);
        assertThat(result).isEqualTo(6);
    }

    @Test
    void zip_four_suppliers_combines() {
        Supplier<Integer> a = () -> 1;
        Supplier<Integer> b = () -> 2;
        Supplier<Integer> c = () -> 3;
        Supplier<Integer> d = () -> 4;
        Function4<Integer, Integer, Integer, Integer, Integer> sum4 = (w, x, y, z) -> w + x + y + z;

        int result = Suppliers.zip(a, b, c, d, sum4);
        assertThat(result).isEqualTo(10);
    }

    @Test
    void zip_propagates_exception_from_supplier() {
        Supplier<Integer> bad = () -> {
            throw new RuntimeException("fail");
        };
        Supplier<Integer> good = () -> 1;
        Function2<Integer, Integer, Integer> sum = Integer::sum;

        assertThatThrownBy(() -> Suppliers.zip(bad, good, sum))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("fail");
    }
}
