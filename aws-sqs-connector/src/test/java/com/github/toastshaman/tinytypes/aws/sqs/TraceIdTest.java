package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.toastshaman.tinytypes.validation.ValidationException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TraceIdTest {

    @Test
    void should_create_trace_id_from_value() {
        var traceId = TraceId.of("my-trace-id");

        assertThat(traceId.unwrap()).isEqualTo("my-trace-id");
    }

    @Test
    void should_generate_random_ulid_trace_id() {
        var traceId = TraceId.random();

        assertThat(traceId.unwrap()).hasSize(16);
        assertThat(traceId.unwrap()).matches("^[0-9a-z]{16}$");
    }

    @Test
    void should_generate_different_random_trace_ids() {
        var first = TraceId.random();
        var second = TraceId.random();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void should_reject_null_value() {
        assertThatThrownBy(() -> TraceId.of(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_reject_blank_value() {
        assertThatThrownBy(() -> TraceId.of("")).isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> TraceId.of("   ")).isInstanceOf(ValidationException.class);
    }

    @Test
    void should_implement_equality() {
        var first = TraceId.of("same-id");
        var second = TraceId.of("same-id");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void should_implement_to_string() {
        var traceId = TraceId.of("my-trace-id");

        assertThat(traceId.toString()).isEqualTo("my-trace-id");
    }
}
