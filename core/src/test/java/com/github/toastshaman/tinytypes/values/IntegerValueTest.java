package com.github.toastshaman.tinytypes.values;

import com.github.toastshaman.tinytypes.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerValueTest {

    @Test
    void immutable_increments() {
        var five = new MyCounter(5);

        var counter = new MyCounter()
                .inc()
                .inc()
                .plus(five)
                .unwrap();

        assertThat(counter).isEqualTo(7);
    }

    @Test
    void can_map_values() {
        var counter = new MyCounter(100)
                .map(v -> v + 100);

        assertThat(counter).isEqualTo(new MyCounter(200));
    }

    private static class MyCounter extends IntegerValue {
        public MyCounter() {
            this(0);
        }

        public MyCounter(Integer initial) {
            super(initial, Validator.AlwaysValid(), Object::toString);
        }

        public MyCounter map(Function<Integer, Integer> mapper) {
            return map(mapper, (value, validator, showFn) -> new MyCounter(value));
        }
    }
}