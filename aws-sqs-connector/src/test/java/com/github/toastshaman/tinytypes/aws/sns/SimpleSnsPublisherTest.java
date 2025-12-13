package com.github.toastshaman.tinytypes.aws.sns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.QUEUE_ARN;

import com.github.toastshaman.tinytypes.aws.sqs.QueueArn;
import com.github.toastshaman.tinytypes.aws.sqs.QueueUrl;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Testcontainers
class SimpleSnsPublisherTest {

    String TEST_TOPIC_NAME = "test-topic";

    String TEST_QUEUE_NAME = "test-queue";

    @Container
    LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack")).withServices("sns", "sqs");

    private TopicArn topicArn;

    private QueueUrl queueUrl;

    @BeforeEach
    void setUp() {
        try (var snsClient = createSnsClient();
                var sqsClient = createSqsClient()) {
            topicArn = createTopic(snsClient);
            queueUrl = createQueue(sqsClient);
            subscribeQueueToTopic(snsClient, sqsClient);
        }
    }

    @AfterEach
    void tearDown() {
        try (var snsClient = createSnsClient();
                var sqsClient = createSqsClient()) {
            snsClient.deleteTopic(it -> it.topicArn(topicArn.unwrap()));
            sqsClient.deleteQueue(it -> it.queueUrl(queueUrl.asString()));
        }
    }

    private SnsClient createSnsClient() {
        return SnsClient.builder()
                .endpointOverride(localstack.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .region(Region.of(localstack.getRegion()))
                .build();
    }

    private SqsClient createSqsClient() {
        return SqsClient.builder()
                .endpointOverride(localstack.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .region(Region.of(localstack.getRegion()))
                .build();
    }

    private TopicArn createTopic(SnsClient client) {
        var response = client.createTopic(it -> it.name(TEST_TOPIC_NAME));
        return TopicArn.of(response.topicArn());
    }

    private QueueUrl createQueue(SqsClient client) {
        var response = client.createQueue(it -> it.queueName(TEST_QUEUE_NAME));
        return QueueUrl.parse(response.queueUrl());
    }

    private void subscribeQueueToTopic(SnsClient snsClient, SqsClient sqsClient) {
        var queueArn = getQueueArn(sqsClient);

        snsClient.subscribe(it -> it.topicArn(topicArn.unwrap()).protocol("sqs").endpoint(queueArn.unwrap()));

        // Allow SNS to send messages to SQS
        var policy = generateSqsPolicyJson(queueArn, topicArn);

        sqsClient.setQueueAttributes(
                it -> it.queueUrl(queueUrl.asString()).attributesWithStrings(Map.of("Policy", policy.toString())));
    }

    private JSONObject generateSqsPolicyJson(QueueArn queueArn, TopicArn topicArn) {
        // spotless:off
        var statement = new JSONObject()
                .put("Effect", "Allow")
                .put("Principal", new JSONObject().put("Service", "sns.amazonaws.com"))
                .put("Action", "sqs:SendMessage")
                .put("Resource", queueArn.unwrap())
                .put("Condition", new JSONObject().put("ArnEquals", new JSONObject().put("aws:SourceArn", topicArn.unwrap())));

        return new JSONObject()
                .put("Version", "2012-10-17")
                .put("Statement", new JSONArray().put(statement));
        // spotless:on
    }

    private QueueArn getQueueArn(SqsClient sqsClient) {
        var response = sqsClient.getQueueAttributes(
                it -> it.queueUrl(queueUrl.asString()).attributeNamesWithStrings(QUEUE_ARN.toString()));

        return QueueArn.of(response.attributes().get(QUEUE_ARN));
    }

    @Test
    void can_publish_messages_to_sns() {
        try (var snsClient = createSnsClient();
                var sqsClient = createSqsClient()) {
            // given
            var publisher = new SimpleSnsPublisher<String>(snsClient, topicArn, it -> it);

            // when
            publisher.publish("Hello World");

            // then
            await().untilAsserted(() -> {
                var messages = sqsClient.receiveMessage(it -> it.queueUrl(queueUrl.asString()));
                assertThat(messages.messages()).hasSize(1);
                assertThat(messages.messages())
                        .first()
                        .extracting(Message::body)
                        .asString()
                        .contains("Hello World");
            });
        }
    }
}
