package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.events.Events;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

public record ForwardToDeadLetterQueueOnExceptionFilter(DeadLetterQueueUrl queueUrl, SqsClient sqs, Events events)
        implements SqsMessageFilter {

    public ForwardToDeadLetterQueueOnExceptionFilter {
        Objects.requireNonNull(queueUrl, "queueUrl must not be null");
        Objects.requireNonNull(sqs, "sqs client must not be null");
        Objects.requireNonNull(events, "events must not be null");
    }

    @Override
    public SqsMessageHandler filter(SqsMessageHandler handler) {
        return messages -> {
            try {
                handler.handle(messages);
            } catch (Exception e) {
                var entries = messages.stream()
                        .map(ForwardToDeadLetterQueueOnExceptionFilter::entryFrom)
                        .toList();

                var build = SendMessageBatchRequest.builder()
                        .queueUrl(queueUrl.unwrap().toString())
                        .entries(entries)
                        .build();

                sqs.sendMessageBatch(build);
            }
        };
    }

    private static SendMessageBatchRequestEntry entryFrom(Message message) {
        var attributes = message.messageAttributes();

        var request = SendMessageBatchRequestEntry.builder()
                .id(message.messageId())
                .messageBody(message.body())
                .messageAttributes(attributes);

        Optional.ofNullable(attributes.get("MessageGroupId"))
                .map(MessageAttributeValue::stringValue)
                .ifPresent(request::messageGroupId);

        Optional.ofNullable(attributes.get("MessageDeduplicationId"))
                .map(MessageAttributeValue::stringValue)
                .ifPresent(request::messageDeduplicationId);

        return request.build();
    }
}
