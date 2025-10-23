package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.ChainingSqsMessageFilter;
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
import java.util.function.Function;
import java.util.stream.Stream;
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
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.Message;

@Testcontainers
class PollingSqsMessageListenerTest {

    public static final String TEST_QUEUE_NAME = "test-queue";
    public static final String TEST_DLQ_QUEUE_NAME = "test-dlq-queue";

    Events events = new PrintStreamEventLogger();

    Faker faker = new Faker(new Random(83726362L));

    @Container
    LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack")).withServices(SQS);

    @BeforeEach
    void setUp() {
        try (var client = createSqsClient()) {
            var queueUrls = getQueueUrls(client);
            var randomNames = someMessages();

            for (String name : randomNames) {
                client.sendMessage(b -> b.queueUrl(queueUrls.getFirst()).messageBody(name));
            }
        }
    }

    @AfterEach
    void tearDown() {
        try (var client = createSqsClient()) {
            List.of(TEST_QUEUE_NAME, TEST_DLQ_QUEUE_NAME).forEach(name -> {
                var response = client.getQueueUrl(builder -> builder.queueName(name));
                client.deleteQueue(builder -> builder.queueUrl(response.queueUrl()));
            });
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

    private List<String> getQueueUrls(SqsClient client) {
        return Stream.of(TEST_QUEUE_NAME, TEST_DLQ_QUEUE_NAME)
                .map(name -> client.createQueue(builder -> builder.queueName(name)))
                .map(CreateQueueResponse::queueUrl)
                .toList();
    }

    private List<String> someMessages() {
        return faker.collection(() -> faker.cat().name()).len(3, 5).generate();
    }

    @Test
    void can_poll_messages_from_sqs() {
        try (var client = createSqsClient()) {
            // given
            var url = getQueueUrls(client).getFirst();

            var queueUrl = new QueueUrl(url);

            var sink = new Sink();

            var chain = MeasuringSqsMessageFilter(events)
                    .andThen(RetryingSqsMessageFilter(builder -> builder.withMaxRetries(3)))
                    .andThen(ChainingSqsMessageFilter(((Function<Message, String>) Message::body).andThen(sink)));

            var listener = new PollingSqsMessageListener(queueUrl, client, events, new Options(5, 10), chain);

            // when
            listener.poll();

            // then
            assertThat(sink.captured).containsExactlyInAnyOrder("Max", "Misty", "Tator Tot", "Simba", "Angel");
        }
    }

    private static class Sink implements Function<String, Void> {

        public final List<String> captured = new ArrayList<>();

        @Override
        public Void apply(String input) {
            captured.add(input);
            return null;
        }
    }
}
