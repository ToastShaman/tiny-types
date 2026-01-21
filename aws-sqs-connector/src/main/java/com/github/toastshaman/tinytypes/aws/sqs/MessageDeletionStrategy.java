package com.github.toastshaman.tinytypes.aws.sqs;

import software.amazon.awssdk.services.sqs.SqsClient;

public sealed interface MessageDeletionStrategy
        permits BatchMessageDeletionStrategy, IndividualMessageDeletionStrategy {

    static BatchMessageDeletionStrategy batch(SqsClient sqs, QueueUrl queueUrl) {
        return new BatchMessageDeletionStrategy(sqs, queueUrl);
    }

    static IndividualMessageDeletionStrategy individual(SqsClient sqs, QueueUrl queueUrl) {
        return new IndividualMessageDeletionStrategy(sqs, queueUrl);
    }
}
