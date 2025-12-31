package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;

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
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Testcontainers
class SimpleSqsPublisherTest {

    SqsHeader<String> MY_HEADER = SqsHeader.text("my-header");

    String TEST_QUEUE_NAME = "test-queue";

    @Container
    LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack")).withServices("sqs");

    @BeforeEach
    void setUp() {
        try (var client = createSqsClient()) {
            createQueue(client);
        }
    }

    @AfterEach
    void tearDown() {
        try (var client = createSqsClient()) {
            var queueUrl = getQueueUrl(client);
            client.deleteQueue(builder -> builder.queueUrl(queueUrl.asString()));
        }
    }

    private SqsClient createSqsClient() {
        return SqsClient.builder()
                .endpointOverride(localstack.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .region(Region.of(localstack.getRegion()))
                .build();
    }

    private QueueUrl getQueueUrl(SqsClient client) {
        var response = client.getQueueUrl(builder -> builder.queueName(TEST_QUEUE_NAME));
        return QueueUrl.parse(response.queueUrl());
    }

    private void createQueue(SqsClient client) {
        var response = client.createQueue(builder -> builder.queueName(TEST_QUEUE_NAME));
        QueueUrl.parse(response.queueUrl());
    }

    @Test
    void can_publish_messages_to_sqs() {
        try (var client = createSqsClient()) {
            // given
            var queueUrl = getQueueUrl(client);

            var publisher = new SimpleSqsPublisher<String>(client, queueUrl, it -> it);

            // when
            publisher.publish("Hello World", MY_HEADER.with("HeaderValue"));

            // then
            var messages = client.receiveMessage(
                    builder -> builder.queueUrl(queueUrl.asString()).maxNumberOfMessages(10));

            assertThat(messages.messages()).hasSize(1);
            assertThat(messages.messages()).first().extracting(Message::body).isEqualTo("Hello World");
        }
    }
}
