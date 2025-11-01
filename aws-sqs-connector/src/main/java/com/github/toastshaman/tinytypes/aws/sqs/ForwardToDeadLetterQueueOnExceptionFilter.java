package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

@SuppressWarnings("ClassCanBeRecord")
public final class ForwardToDeadLetterQueueOnExceptionFilter implements SqsMessagesFilter {

    private final DeadLetterQueueUrl queueUrl;

    private final SqsClient sqs;

    public ForwardToDeadLetterQueueOnExceptionFilter(DeadLetterQueueUrl queueUrl, SqsClient sqs) {
        this.queueUrl = Objects.requireNonNull(queueUrl, "queueUrl must not be null");
        this.sqs = Objects.requireNonNull(sqs, "sqs client must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            try {
                next.handle(messages);
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
