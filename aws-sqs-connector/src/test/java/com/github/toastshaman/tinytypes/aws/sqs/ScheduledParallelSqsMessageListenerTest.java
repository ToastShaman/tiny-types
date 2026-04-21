package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.ParallelSqsMessageListenerThreadType.VIRTUAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledParallelSqsMessageListenerTest {

    @Test
    void schedules_parallel_polls_and_aggregates_messages_across_threads() {
        var threads = 4;
        var pollInvocations = new AtomicInteger();
        var threadIds = ConcurrentHashMap.newKeySet();

        SqsMessageListener delegate = () -> {
            pollInvocations.incrementAndGet();
            threadIds.add(Thread.currentThread().threadId());
            System.out.println("polling on " + Thread.currentThread());
            return 1;
        };

        var listenerOptions = ParallelSqsMessageListenerOptions.builder()
                .threads(threads)
                .threadType(VIRTUAL)
                .build();

        var parallel = new ParallelSqsMessageListener(delegate, listenerOptions);

        var scheduledOptions = ScheduledSqsMessageListenerOptions.builder()
                .delay(Duration.ofMillis(50))
                .shutdownTimeout(Duration.ofMillis(200))
                .build();

        try (var scheduled = new ScheduledSqsMessageListener(parallel, scheduledOptions)) {
            scheduled.start();

            // Wait until the parallel listener has fired enough times that, given `threads` polls per
            // tick, we have observed multiple ticks worth of delegate invocations.
            await().atMost(Duration.ofSeconds(3))
                    .untilAsserted(() -> assertThat(pollInvocations.get()).isGreaterThanOrEqualTo(threads * 2));

            scheduled.stop();
            assertThat(scheduled.isRunning()).isFalse();
        }

        // Each tick must have invoked the delegate once per configured thread, so the total must be a
        // multiple of `threads`.
        assertThat(pollInvocations.get() % threads).isZero();

        // We expect more than one distinct thread to have been used across all the parallel polls.
        assertThat(threadIds.size()).isGreaterThan(1);
    }
}
