package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

@SuppressWarnings("ClassCanBeRecord")
public final class SimpleSqsPublisher<T> implements SqsPublisher<T> {

    private final SqsClient client;

    private final QueueUrl queueUrl;

    private final MessageSerializer<T> serializer;

    public SimpleSqsPublisher(SqsClient client, QueueUrl queueUrl, MessageSerializer<T> serializer) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.queueUrl = Objects.requireNonNull(queueUrl, "queueUrl must not be null");
        this.serializer = Objects.requireNonNull(serializer, "serializer must not be null");
    }

    @Override
    @SafeVarargs
    public final void publish(T message, Map.Entry<String, MessageAttributeValue>... attributes) {
        Objects.requireNonNull(message, "message must not be null");

        var messageAttributes = Map.ofEntries(attributes);

        client.sendMessage(builder -> builder.queueUrl(queueUrl.unwrap().toString())
                .messageBody(serializer.serialize(message))
                .messageAttributes(messageAttributes));
    }

    @Override
    @SafeVarargs
    public final void publish(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes) {
        Objects.requireNonNull(messages, "messages must not be null");

        var messageAttributes = Map.ofEntries(attributes);

        var entries = messages.stream()
                .map(serializer::serialize)
                .map(it -> SendMessageBatchRequestEntry.builder()
                        .messageBody(it)
                        .messageAttributes(messageAttributes)
                        .build())
                .toList();

        var request = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl.unwrap().toString())
                .entries(entries)
                .build();

        client.sendMessageBatch(request);
    }
}
