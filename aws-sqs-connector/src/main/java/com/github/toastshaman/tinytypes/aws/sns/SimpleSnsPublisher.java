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
    public void publish(T message, Map.Entry<String, MessageAttributeValue>... attributes) {
        var messageAttributes = Map.ofEntries(attributes);

        client.publish(builder -> builder.topicArn(topicArn.unwrap())
                .message(serializer.serialize(message))
                .messageAttributes(messageAttributes));
    }

    @Override
    @SafeVarargs
    public final void publish(List<T> messages, Map.Entry<String, MessageAttributeValue>... attributes) {
        var messageAttributes = Map.ofEntries(attributes);

        var entries = messages.stream()
                .map(serializer::serialize)
                .map(it -> PublishBatchRequestEntry.builder()
                        .message(it)
                        .messageAttributes(messageAttributes)
                        .build())
                .toList();

        var request = PublishBatchRequest.builder()
                .topicArn(topicArn.unwrap())
                .publishBatchRequestEntries(entries)
                .build();

        client.publishBatch(request);
    }
}
