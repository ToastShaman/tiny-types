package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.toastshaman.tinytypes.validation.ValidationException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class SpanIdTest {

    @Test
    void should_create_span_id_from_value() {
        var spanId = SpanId.of("my-span-id");

        assertThat(spanId.unwrap()).isEqualTo("my-span-id");
    }

    @Test
    void should_generate_random_ulid_span_id() {
        var spanId = SpanId.random();

        assertThat(spanId.unwrap()).hasSize(26);
        assertThat(spanId.unwrap()).matches("^[0-9a-z]{26}$");
    }

    @Test
    void should_generate_different_random_span_ids() {
        var first = SpanId.random();
        var second = SpanId.random();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> SpanId.of(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> SpanId.of("")).isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> SpanId.of("   ")).isInstanceOf(ValidationException.class);
    }

    @Test
    void should_implement_equality() {
        var first = SpanId.of("same-id");
        var second = SpanId.of("same-id");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void should_implement_to_string() {
        var spanId = SpanId.of("my-span-id");

        assertThat(spanId.toString()).isEqualTo("my-span-id");
    }
}
