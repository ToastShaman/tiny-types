package com.github.toastshaman.tinytypes.aws.sns;

import com.github.toastshaman.tinytypes.aws.sqs.MessageSerializer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishBatchRequest;
import software.amazon.awssdk.services.sns.model.PublishBatchRequestEntry;

public final class SimpleSnsPublisher<T> implements SnsPublisher<T> {

    private final SnsClient client;

    private final TopicArn topicArn;

    private final MessageSerializer<T> serializer;

    public SimpleSnsPublisher(SnsClient client, TopicArn topicArn, MessageSerializer<T> serializer) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.topicArn = Objects.requireNonNull(topicArn, "topic arn must not be null");
        this.serializer = Objects.requireNonNull(serializer, "serializer must not be null");
    }

    @Override
    public void publish(T message, Map<String, MessageAttributeValue> attributes) {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");

        client.publish(builder -> builder.topicArn(topicArn.unwrap())
                .message(serializer.apply(message))
                .messageAttributes(attributes));
    }

    @Override
    public void publish(List<T> messages, Map<String, MessageAttributeValue> attributes) {
        Objects.requireNonNull(messages, "messages must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");

        var entries = messages.stream()
                .map(serializer)
                .map(it -> PublishBatchRequestEntry.builder()
                        .message(it)
                        .messageAttributes(attributes)
                        .build())
                .toList();

        var request = PublishBatchRequest.builder()
                .topicArn(topicArn.unwrap())
                .publishBatchRequestEntries(entries)
                .build();

        client.publishBatch(request);
    }
}
