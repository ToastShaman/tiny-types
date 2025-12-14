package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Instant;
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

            var extractedValue = TEST_HEADER.get(message);

            assertThat(extractedValue).isEqualTo("header-value");
        }

        @Test
        void can_set_header() {
            var message = Message.builder()
                    .messageAttributes(Map.ofEntries(TEST_HEADER.reverseGet("header-value")))
                    .build();

            var extractedValue = TEST_HEADER.get(message);

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
            var extractedValue = timestampHeader.get(message);

            assertThat(extractedValue).isEqualTo("2024-06-01T12:34:56Z");
        }
    }

    @Test
    void apply_sets_multiple_headers_on_message() {
        var original = Message.builder().build();

        var messageWithHeaders = SqsHeader.apply(
                        SqsHeaders.EVENT_TIMESTAMP.with(Instant.now()),
                        SqsHeaders.EVENT_ID.with("event-id-123"),
                        SqsHeaders.EVENT_TYPE.with("event-type-xyz"))
                .apply(original);

        assertThat(SqsHeaders.EVENT_ID.get(messageWithHeaders)).isEqualTo("event-id-123");
    }
}
