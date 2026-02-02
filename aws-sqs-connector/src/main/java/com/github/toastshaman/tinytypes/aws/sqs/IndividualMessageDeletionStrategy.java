package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.Objects;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

public final class IndividualMessageDeletionStrategy implements MessageDeletionStrategy {

    private final SqsClient sqs;

    private final QueueUrl queueUrl;

    public IndividualMessageDeletionStrategy(SqsClient sqs, QueueUrl queueUrl) {
        this.sqs = Objects.requireNonNull(sqs, "sqs client must not be null");
        this.queueUrl = Objects.requireNonNull(queueUrl, "queue url must not be null");
    }

    public void delete(Message message) {
        sqs.deleteMessage(builder -> builder.queueUrl(queueUrl.asString()).receiptHandle(message.receiptHandle()));
    }
}
