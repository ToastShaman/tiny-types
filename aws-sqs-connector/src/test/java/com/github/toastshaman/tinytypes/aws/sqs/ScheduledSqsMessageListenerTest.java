package com.github.toastshaman.tinytypes.aws.sqs;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.*;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledSqsMessageListenerTest {

    private static class TestSqsMessageListener implements SqsMessageListener {
        final AtomicInteger pollCount = new AtomicInteger();
        final boolean throwException;

        TestSqsMessageListener(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        public void poll() {
            pollCount.incrementAndGet();
            if (throwException) {
                throw new RuntimeException("Test exception");
            }
        }
    }

    ScheduledSqsMessageListenerOptions options = ScheduledSqsMessageListenerOptions.builder()
            .delay(Duration.ofMillis(50))
            .shutdownTimeout(Duration.ofMillis(100))
            .build();

    @Test
    void start_should_schedule_polling_and_set_running_true() {
        var listener = new TestSqsMessageListener(false);

        try (var scheduledListener = new ScheduledSqsMessageListener(listener, options)) {
            scheduledListener.start();

            await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
                assertTrue(scheduledListener.isRunning());
                assertTrue(listener.pollCount.get() >= 2);
            });

            scheduledListener.stop();
        }
    }

    @Test
    void stop_should_shutdown_scheduler_and_set_running_false() {
        var listener = new TestSqsMessageListener(false);

        try (var scheduledListener = new ScheduledSqsMessageListener(listener, options)) {
            scheduledListener.start();

            await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
                assertTrue(scheduledListener.isRunning());
            });

            scheduledListener.stop();
            assertFalse(scheduledListener.isRunning());
        }
    }

    @Test
    void poll_should_handle_exceptions_and_continue_running() throws InterruptedException {
        var listener = new TestSqsMessageListener(true);

        try (var scheduledListener = new ScheduledSqsMessageListener(listener, options)) {
            scheduledListener.start();

            await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertTrue(listener.pollCount.get() >= 2));

            assertTrue(scheduledListener.isRunning());

            scheduledListener.stop();
        }
    }

    @Test
    void close_should_stop_the_listener() {
        var listener = new TestSqsMessageListener(false);
        try (var scheduledListener = new ScheduledSqsMessageListener(listener, options)) {
            scheduledListener.start();
            scheduledListener.close();
            assertFalse(scheduledListener.isRunning());
        }
    }
}
