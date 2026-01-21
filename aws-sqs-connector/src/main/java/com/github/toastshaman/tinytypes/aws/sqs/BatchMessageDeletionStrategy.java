package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class BatchMessageDeletionStrategy implements MessageDeletionStrategy {

    private final SqsClient sqs;

    private final QueueUrl queueUrl;

    public BatchMessageDeletionStrategy(SqsClient sqs, QueueUrl queueUrl) {
        this.sqs = Objects.requireNonNull(sqs, "sqs client must not be null");
        this.queueUrl = Objects.requireNonNull(queueUrl, "queue url must not be null");
    }

    public void delete(List<Message> messages) {
        var entries = messages.stream()
                .map(message -> DeleteMessageBatchRequestEntry.builder()
                        .id(message.messageId())
                        .receiptHandle(message.receiptHandle())
                        .build())
                .toList();

        sqs.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl.asString())
                .entries(entries)
                .build());
    }
}
