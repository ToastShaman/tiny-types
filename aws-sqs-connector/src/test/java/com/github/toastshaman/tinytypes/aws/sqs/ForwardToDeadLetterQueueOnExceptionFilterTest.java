package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.ForwardToDeadLetterQueueOnExceptionFilter.isInstanceOfOrHasCause;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.ForwardToDeadLetterQueueOnExceptionFilter;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.MeasuringSqsMessageFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toastshaman.tinytypes.events.Events;
import com.github.toastshaman.tinytypes.events.PrintStreamEventLogger;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Testcontainers
@DisplayNameGeneration(ReplaceUnderscores.class)
class ForwardToDeadLetterQueueOnExceptionFilterTest {

    String QUEUE_NAME = "test-queue";

    String DLQ_QUEUE_NAME = "test-queue-dlq";

    Events events = new PrintStreamEventLogger();

    Faker faker = new Faker(new Random(83726362L));

    PollingSqsMessageListenerOptions options = new PollingSqsMessageListenerOptions(5, 10);

    @Container
    LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack")).withServices("sqs");

    @BeforeEach
    void setUp() {
        try (var client = createSqsClient()) {
            List.of(QUEUE_NAME, DLQ_QUEUE_NAME).forEach(name -> createQueue(client, name));

            var queueUrl = getQueueUrl(client).asString();
            var randomName = faker.cat().name();
            client.sendMessage(it -> it.queueUrl(queueUrl).messageBody(randomName));
        }
    }

    @AfterEach
    void tearDown() {
        try (var client = createSqsClient()) {
            var queueUrl = getQueueUrl(client).asString();
            client.deleteQueue(it -> it.queueUrl(queueUrl));

            var dlqQueueUrl = getDlqQueueUrl(client).asString();
            client.deleteQueue(it -> it.queueUrl(dlqQueueUrl));
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
        var response = client.getQueueUrl(builder -> builder.queueName(QUEUE_NAME));
        return QueueUrl.parse(response.queueUrl());
    }

    private DeadLetterQueueUrl getDlqQueueUrl(SqsClient client) {
        var response = client.getQueueUrl(builder -> builder.queueName(DLQ_QUEUE_NAME));
        return DeadLetterQueueUrl.parse(response.queueUrl());
    }

    private QueueUrl createQueue(SqsClient client, String queueName) {
        var response = client.createQueue(builder -> builder.queueName(queueName));
        return QueueUrl.parse(response.queueUrl());
    }

    @Test
    void forwards_messages_to_dlq() {
        try (var client = createSqsClient()) {
            // given
            var queueUrl = getQueueUrl(client);

            var dlqQueueUrl = getDlqQueueUrl(client);

            var chain = MeasuringSqsMessageFilter(events)
                    .andThen(ForwardToDeadLetterQueueOnExceptionFilter(dlqQueueUrl, client))
                    .andThen(SqsMessagesHandler.forEach(_ -> {
                        throw new RuntimeException("Simulated processing failure");
                    }));

            var deletionStrategy = MessageDeletionStrategy.individual(client, queueUrl);

            var listener = new PollingSqsMessageListener(queueUrl, client, events, options, deletionStrategy, chain);

            // when
            listener.poll();

            // then
            var dlqMessages = client.receiveMessage(it -> it.queueUrl(dlqQueueUrl.asString())
                            .maxNumberOfMessages(10)
                            .messageAttributeNames("All"))
                    .messages();

            assertThat(dlqMessages).hasSize(1).first().satisfies(message -> assertThat(message.body())
                    .isEqualTo("Lady Rainicorn"));
        }
    }

    @Test
    void only_forward_when_the_filter_matches() {
        try (var client = createSqsClient()) {
            // given
            var queueUrl = getQueueUrl(client);

            var dlqQueueUrl = getDlqQueueUrl(client);

            var chain = MeasuringSqsMessageFilter(events)
                    .andThen(ForwardToDeadLetterQueueOnExceptionFilter(
                            dlqQueueUrl, client, e -> e instanceof RetriesExceededException))
                    .andThen(SqsMessagesHandler.forEach(_ -> {
                        throw new RetriesExceededException("Simulated retries exceeded");
                    }));

            var deletionStrategy = MessageDeletionStrategy.individual(client, queueUrl);

            var listener = new PollingSqsMessageListener(queueUrl, client, events, options, deletionStrategy, chain);

            // when
            listener.poll();

            // then
            var dlqMessages = client.receiveMessage(it -> it.queueUrl(dlqQueueUrl.asString())
                            .maxNumberOfMessages(10)
                            .messageAttributeNames("All"))
                    .messages();

            assertThat(dlqMessages).hasSize(1).first().satisfies(message -> assertThat(message.body())
                    .isEqualTo("Lady Rainicorn"));
        }
    }

    @Test
    void do_not_forward_if_filter_does_not_match() {
        try (var client = createSqsClient()) {
            // given
            var queueUrl = getQueueUrl(client);

            var dlqQueueUrl = getDlqQueueUrl(client);

            var chain = MeasuringSqsMessageFilter(events)
                    .andThen(ForwardToDeadLetterQueueOnExceptionFilter(dlqQueueUrl, client, e -> false))
                    .andThen(SqsMessagesHandler.forEach(_ -> {
                        throw new IllegalArgumentException("Simulated retries exceeded");
                    }));

            var deletionStrategy = MessageDeletionStrategy.individual(client, queueUrl);

            var listener = new PollingSqsMessageListener(queueUrl, client, events, options, deletionStrategy, chain);

            // when
            listener.poll();

            // then
            var dlqMessages = client.receiveMessage(it -> it.queueUrl(dlqQueueUrl.asString())
                            .maxNumberOfMessages(10)
                            .messageAttributeNames("All"))
                    .messages();

            assertThat(dlqMessages).hasSize(0);
        }
    }

    static class RetriesExceededException extends RuntimeException {
        public RetriesExceededException(String message) {
            super(message);
        }
    }

    @Test
    void test_IsInstanceOfOrHasCause() {
        // Direct match
        Exception directMatch = new IllegalArgumentException("test");
        assertTrue(isInstanceOfOrHasCause(IllegalArgumentException.class).test(directMatch));

        // Subclass match
        assertTrue(isInstanceOfOrHasCause(RuntimeException.class).test(directMatch));
        assertTrue(isInstanceOfOrHasCause(Exception.class).test(directMatch));

        // No match
        assertFalse(isInstanceOfOrHasCause(IOException.class).test(directMatch));

        // Cause match
        IOException cause = new IOException("root cause");
        Exception withCause = new RuntimeException("wrapper", cause);
        assertTrue(isInstanceOfOrHasCause(IOException.class).test(withCause));
        assertTrue(isInstanceOfOrHasCause(RuntimeException.class).test(withCause));

        // Deep cause chain
        SQLException rootCause = new SQLException("database error");
        IOException middleCause = new IOException("io error", rootCause);
        Exception deepChain = new RuntimeException("wrapper", middleCause);
        assertTrue(isInstanceOfOrHasCause(SQLException.class).test(deepChain));
        assertTrue(isInstanceOfOrHasCause(IOException.class).test(deepChain));
        assertFalse(isInstanceOfOrHasCause(IllegalStateException.class).test(deepChain));

        // Null checks
        assertFalse(isInstanceOfOrHasCause(Exception.class).test(null));
        assertFalse(isInstanceOfOrHasCause(null).test(directMatch));
        assertFalse(isInstanceOfOrHasCause(null).test(null));

        // No cause
        Exception noCause = new Exception("no cause");
        assertTrue(isInstanceOfOrHasCause(Exception.class).test(noCause));
        assertFalse(isInstanceOfOrHasCause(IOException.class).test(noCause));
    }
}
