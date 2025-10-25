package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.MeasuringSqsMessageFilter;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.RetryingSqsMessageFilter;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.toastshaman.tinytypes.events.Events;
import com.github.toastshaman.tinytypes.events.PrintStreamEventLogger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.datafaker.Faker;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RetryingSqsMessageFilterTest {

    Events events = new PrintStreamEventLogger(System.out);

    Faker faker = new Faker(new Random(578));

    @Test
    void retries_on_exceptions() {
        var attempt = new AtomicInteger(0);

        var chain = MeasuringSqsMessageFilter(events)
                .andThen(RetryingSqsMessageFilter(policy -> policy.withMaxRetries(3)
                        .onFailure(_ -> System.out.println("Failed"))
                        .onRetry(_ -> System.out.println("Retrying..."))))
                .andThen(_ -> {
                    var i = attempt.incrementAndGet();
                    if (i < 3) {
                        System.out.printf("Attempt %d failed.%n", i);
                        throw new RuntimeException("Simulated failure");
                    }
                    System.out.printf("Attempt %d succeeded.%n", i);
                });

        var messages = List.of(aMessage(), aMessage(), aMessage());

        chain.handle(messages);

        assertThat(attempt.get())
                .describedAs("should have failed 2 times and processed 3")
                .isEqualTo(3);
    }

    Message aMessage() {
        return Message.builder()
                .messageId(faker.internet().uuid())
                .body(new JSONObject().put("message", faker.cat().name()).toString())
                .build();
    }
}
