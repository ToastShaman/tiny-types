package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

public final class SimpleSqsSender<T> implements SqsSender<T> {

    private final SqsClient client;

    private final QueueUrl queueUrl;

    private final MessageSerializer<T> serializer;

    public SimpleSqsSender(SqsClient client, QueueUrl queueUrl, MessageSerializer<T> serializer) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.queueUrl = Objects.requireNonNull(queueUrl, "queueUrl must not be null");
        this.serializer = Objects.requireNonNull(serializer, "serializer must not be null");
    }

    @Override
    public void send(T message, Map<String, MessageAttributeValue> attributes) {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");

        client.sendMessage(builder -> builder.queueUrl(queueUrl.unwrap().toString())
                .messageBody(serializer.apply(message))
                .messageAttributes(attributes));
    }

    @Override
    public void send(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        Objects.requireNonNull(messages, "messages must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");

        var entries = messages.stream()
                .map(serializer)
                .map(it -> SendMessageBatchRequestEntry.builder()
                        .messageBody(it)
                        .messageAttributes(attributes)
                        .build())
                .toList();

        var request = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl.unwrap().toString())
                .entries(entries)
                .build();

        client.sendMessageBatch(request);
    }
}
