package com.github.toastshaman.tinytypes.values;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class IntegerValueTest {

    @Test
    void equals_increments() {
        var first = new MyCounter(5);
        var second = new MyCounter(5);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void can_map_values() {
        var counter = new MyCounter(100).map(v -> v + 100);
        assertThat(counter).isEqualTo(new MyCounter(200));
    }

    @Test
    void can_compare_to() {
        var first = new MyCounter(100);
        var second = new MyCounter(100);

        assertThat(first.compareTo(second)).isEqualTo(0);
    }

    private static class MyCounter extends IntegerValue {
        public MyCounter(Integer initial) {
            super(initial, Validator.AlwaysValid(), Object::toString);
        }

        public MyCounter map(Function<Integer, Integer> mapper) {
            return new MyCounter(mapper.apply(value));
        }
    }
}
