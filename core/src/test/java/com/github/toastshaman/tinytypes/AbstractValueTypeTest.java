package com.github.toastshaman.tinytypes;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.validation.Validator;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class AbstractValueTypeTest {

    private final Firstname dave = new Firstname("Dave");

    @Test
    void can_transform() {
        assertThat(dave.<String>transform(String::toUpperCase)).isEqualTo("DAVE");
    }

    @Test
    void can_peek() {
        dave.peek(it -> assertThat(it).isEqualTo("Dave"));
    }

    @Test
    void can_unwrap() {
        assertThat(dave.unwrap()).isInstanceOf(String.class).isEqualTo("Dave");
    }

    @Test
    void are_equal() {
        assertThat(new Firstname("Dave")).isEqualTo(dave);
    }

    @Test
    void can_show() {
        var daveWithShowFn = new Firstname("DAVE", String::toLowerCase);
        assertThat(daveWithShowFn.toString()).isEqualTo("dave");
    }

    private static class Firstname extends AbstractValueType<String> {

        public Firstname(String value) {
            super(value, Validator.AlwaysValid(), it -> it);
        }

        public Firstname(String value, Function<String, String> f) {
            super(value, Validator.AlwaysValid(), f);
        }
    }
}
