package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.DelegatingSqsMessageHandler;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.MeasuringSqsMessageFilter;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.RetryingSqsMessageFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.github.toastshaman.tinytypes.aws.sqs.PollingSqsMessageListener.Options;
import com.github.toastshaman.tinytypes.events.Events;
import com.github.toastshaman.tinytypes.events.PrintStreamEventLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Testcontainers
class PollingSqsMessageListenerTest {

    String TEST_QUEUE_NAME = "test-queue";

    Events events = new PrintStreamEventLogger();

    Faker faker = new Faker(new Random(83726362L));

    Options options = new Options(5, 10);

    @Container
    LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack")).withServices(SQS);

    @BeforeEach
    void setUp() {
        try (var client = createSqsClient()) {
            var queueUrl = createQueue(client);
            var randomNames = someMessages();

            for (String name : randomNames) {
                client.sendMessage(b -> b.queueUrl(queueUrl.asString()).messageBody(name));
            }
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

    private QueueUrl createQueue(SqsClient client) {
        var response = client.createQueue(builder -> builder.queueName(TEST_QUEUE_NAME));
        return QueueUrl.parse(response.queueUrl());
    }

    private List<String> someMessages() {
        return faker.collection(() -> faker.cat().name()).len(3, 5).generate();
    }

    @Test
    void can_poll_messages_from_sqs() {
        try (var client = createSqsClient()) {
            // given
            var queueUrl = getQueueUrl(client);

            var captured = new ArrayList<String>();

            var chain = MeasuringSqsMessageFilter(events)
                    .andThen(RetryingSqsMessageFilter(builder -> builder.withMaxRetries(3)))
                    .andThen(DelegatingSqsMessageHandler(
                            SqsMessageHandler.fromFunction(Message::body).andThen(captured::add)));

            var listener = new PollingSqsMessageListener(queueUrl, client, events, options, chain);

            // when
            listener.poll();

            // then
            assertThat(captured).containsExactlyInAnyOrder("Max", "Misty", "Tator Tot", "Simba", "Angel");
        }
    }
}
