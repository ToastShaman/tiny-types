package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

@DisplayNameGeneration(ReplaceUnderscores.class)
class SqsHeaderTest {

    public static final SqsHeader<String> TEST_HEADER = SqsHeader.text("x-my-test-header");

    @Nested
    class Text {
        @Test
        void can_extract_header() {
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
        void can_set_header() {
            var message = Message.builder()
                    .messageAttributes(Map.ofEntries(
                            Map.entry(TEST_HEADER.name(), TEST_HEADER.encode().apply("header-value"))))
                    .build();

            var extractedValue = TEST_HEADER.from(message);

            assertThat(extractedValue).isEqualTo("header-value");
        }
    }

    @Nested
    class Timestamp {
        @Test
        void can_extract_header() {
            var message = Message.builder()
                    .messageAttributes(Map.of(
                            "x-my-test-header",
                            MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue("2024-06-01T12:34:56Z")
                                    .build()))
                    .build();

            var timestampHeader = SqsHeader.timestamp("x-my-test-header");
            var extractedValue = timestampHeader.from(message);

            assertThat(extractedValue).isEqualTo("2024-06-01T12:34:56Z");
        }
    }

    @Test
    void returns_the_encoded__enum_value() {
        enum X {
            A,
            B,
            C
        }

        var header = SqsHeader.enumOf("x-enum-header", X.class);

        var entry = header.with(X.B);

        assertThat(entry.getKey()).isEqualTo("x-enum-header");
        assertThat(entry.getValue().stringValue()).isEqualTo("B");
        assertThat(entry.getValue().dataType()).isEqualTo("String");

        var message = Message.builder()
                .messageAttributes(Map.ofEntries(Map.entry(
                        header.name(),
                        MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue("C")
                                .build())))
                .build();

        var extractedValue = header.from(message);

        assertThat(extractedValue).isEqualTo(X.C);
    }
}
