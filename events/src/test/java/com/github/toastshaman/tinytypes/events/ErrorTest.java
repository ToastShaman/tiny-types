package com.github.toastshaman.tinytypes.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ErrorTest {

    @Test
    void should_create_error_with_message_and_cause() {
        var message = "Something went wrong";
        var cause = new RuntimeException("Root cause");

        var error = new Error(message, cause);

        assertThat(error.message()).isEqualTo(message);
        assertThat(error.cause()).isEqualTo(cause);
    }

    @Test
    void should_create_error_with_message_only() {
        var message = "Something went wrong";

        var error = new Error(message, null);

        assertThat(error.message()).isEqualTo(message);
        assertThat(error.cause()).isNull();
    }

    @Test
    void should_throw_exception_when_message_is_null() {
        assertThatThrownBy(() -> new Error(null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_return_error_category() {
        var error = new Error("test message", null);

        assertThat(error.category()).isEqualTo(EventCategory.ERROR);
    }

    @Test
    void should_create_error_from_message_using_factory_method() {
        var message = "Factory created error";

        var error = Error.from(message);

        assertThat(error.message()).isEqualTo(message);
        assertThat(error.cause()).isNull();
    }

    @Test
    void should_create_error_from_message_and_throwable_using_factory_method() {
        var message = "Factory created error";
        var throwable = new IllegalArgumentException("Invalid argument");

        var error = Error.from(message, throwable);

        assertThat(error.message()).isEqualTo(message);
        assertThat(error.cause()).isEqualTo(throwable);
    }

    @Test
    void should_implement_event_interface() {
        var error = Error.from("test");

        assertThat(error).isInstanceOf(Event.class);
    }

    @Test
    void should_support_adding_metadata() {
        var error = Error.from("test error");

        var eventWithMetadata = error.addMetadata("severity", "high");

        assertThat(eventWithMetadata)
                .isInstanceOfSatisfying(MetadataEvent.class, metadataEvent -> assertThat(metadataEvent.metadata())
                        .containsEntry("severity", "high"));
    }

    @Test
    void should_have_proper_equality_semantics() {
        var message = "Same message";
        var cause = new RuntimeException("Same cause");

        var error1 = new Error(message, cause);
        var error2 = new Error(message, cause);
        var error3 = Error.from(message, cause);

        assertThat(error1).isEqualTo(error2);
        assertThat(error1).isEqualTo(error3);
        assertThat(error1.hashCode()).isEqualTo(error2.hashCode());
    }

    @Test
    void should_have_meaningful_string_representation() {
        var message = "Test error message";
        var cause = new RuntimeException("Test cause");

        var error = new Error(message, cause);

        assertThat(error.toString()).contains(message).contains("RuntimeException");
    }
}
