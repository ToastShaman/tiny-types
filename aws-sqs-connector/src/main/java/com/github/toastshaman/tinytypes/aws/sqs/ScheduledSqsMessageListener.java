package com.github.toastshaman.tinytypes.aws.sqs;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScheduledSqsMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ScheduledSqsMessageListener.class);

    private final SqsMessageListener listener;

    private final Options options;

    private final ScheduledExecutorService scheduler;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public record Options(Duration delay, Duration shutdownTimeout) {
        public Options {
            Objects.requireNonNull(delay, "delay must not be null");
            Objects.requireNonNull(shutdownTimeout, "shutdown timeout must not be null");
        }
    }

    public ScheduledSqsMessageListener(SqsMessageListener listener, Options options) {
        this.listener = Objects.requireNonNull(listener, "listener must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            scheduler.scheduleWithFixedDelay(
                    () -> {
                        try {
                            listener.poll();
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    },
                    options.delay.toMillis(),
                    options.delay().toMillis(),
                    MILLISECONDS);
            running.set(true);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(options.shutdownTimeout.toMillis(), MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            running.set(false);
        }
    }
}
