package com.github.toastshaman.tinytypes.aws.sns;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

@DisplayNameGeneration(ReplaceUnderscores.class)
class SnsHeaderTest {

    public static final SnsHeader<String> TEST_HEADER = SnsHeader.text("x-my-test-header");

    @Test
    void rejects_null_name() {
        assertThatThrownBy(() -> SnsHeader.text(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("name must not be null");
    }

    @Test
    void rejects_blank_name() {
        assertThatThrownBy(() -> SnsHeader.text(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    void rejects_whitespace_only_name() {
        assertThatThrownBy(() -> SnsHeader.text("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    void can_extract_header_from_message() {
        var message = Message.builder()
                .messageAttributes(Map.of(
                        "x-my-test-header",
                        MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue("header-value")
                                .build()))
                .build();

        var extractedValue = TEST_HEADER.from(message);

        assertThat(extractedValue).isEqualTo("header-value");
    }

    @Test
    void can_create_attribute_entry_with_value() {
        var entry = TEST_HEADER.with("my-value");

        assertThat(entry.getKey()).isEqualTo("x-my-test-header");
        assertThat(entry.getValue().stringValue()).isEqualTo("my-value");
        assertThat(entry.getValue().dataType()).isEqualTo("String");
    }

    @Test
    void throws_when_header_is_missing_from_message() {
        var message = Message.builder().messageAttributes(Map.of()).build();

        assertThatThrownBy(() -> TEST_HEADER.from(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message is missing expected attribute: x-my-test-header");
    }

    @Test
    void can_round_trip_header_value() {
        var message = Message.builder()
                .messageAttributes(Map.ofEntries(Map.entry(
                        TEST_HEADER.name(),
                        MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue("round-trip-value")
                                .build())))
                .build();

        var extractedValue = TEST_HEADER.from(message);

        assertThat(extractedValue).isEqualTo("round-trip-value");
    }

    @Test
    void returns_the_header_name() {
        assertThat(TEST_HEADER.name()).isEqualTo("x-my-test-header");
    }
}
