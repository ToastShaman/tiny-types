package com.github.toastshaman.tinytypes.values;

import org.junit.jupiter.api.Test;

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

    private static class MyCounter extends IntegerValue {
        public MyCounter() {
            super(0);
        }

        public MyCounter(Integer initial) {
            super(initial);
        }
    }
}