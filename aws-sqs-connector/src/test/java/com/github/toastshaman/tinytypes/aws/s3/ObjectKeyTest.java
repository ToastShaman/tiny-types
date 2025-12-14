package com.github.toastshaman.tinytypes.aws.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ObjectKeyTest {

    @Test
    void creates_object_key_from_valid_parts() {
        var key = new ObjectKey(List.of("foo", "bar"));
        assertThat(key.parts()).containsExactly("foo", "bar");
        assertThat(key.asString()).isEqualTo("foo/bar");
    }

    @Test
    void throws_when_parts_is_null() {
        assertThatThrownBy(() -> new ObjectKey(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void throws_when_parts_is_empty() {
        assertThatThrownBy(() -> new ObjectKey(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void throws_when_any_part_is_blank() {
        assertThatThrownBy(() -> new ObjectKey(List.of("foo", " ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot contain null or blank strings");
    }

    @Test
    void throws_when_any_part_contains_slash() {
        assertThatThrownBy(() -> new ObjectKey(List.of("foo/bar")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot contain null or blank strings");
    }

    @Test
    void append_adds_part_to_key() {
        var key = new ObjectKey(List.of("foo")).append("bar");
        assertThat(key.parts()).containsExactly("foo", "bar");
        assertThat(key.asString()).isEqualTo("foo/bar");
    }

    @Test
    void asString_joins_parts_with_slash() {
        var key = new ObjectKey(List.of("a", "b", "c"));
        assertThat(key.asString()).isEqualTo("a/b/c");
    }

    @Test
    void of_creates_key_from_varargs() {
        var key = ObjectKey.of("x", "y");
        assertThat(key.parts()).containsExactly("x", "y");
        assertThat(key.asString()).isEqualTo("x/y");
    }
}
