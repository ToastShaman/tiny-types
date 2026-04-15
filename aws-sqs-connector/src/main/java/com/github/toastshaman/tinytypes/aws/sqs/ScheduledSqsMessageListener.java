package com.github.toastshaman.tinytypes.aws.sqs;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScheduledSqsMessageListener implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ScheduledSqsMessageListener.class);

    private final SqsMessageListener listener;

    private final ScheduledSqsMessageListenerOptions options;

    private final ScheduledExecutorService scheduler;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public ScheduledSqsMessageListener(SqsMessageListener listener, ScheduledSqsMessageListenerOptions options) {
        this.listener = Objects.requireNonNull(listener, "listener must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            scheduleNextPoll(0);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        running.set(false);
        scheduler.shutdown();
        awaitTermination(scheduler);
    }

    private void scheduleNextPoll(long delayMillis) {
        if (!running.get()) {
            return;
        }

        scheduler.schedule(this::runPoll, delayMillis, MILLISECONDS);
    }

    private void runPoll() {
        if (!running.get()) {
            return;
        }

        try {
            var handledMessages = pollAndCountMessages();
            scheduleNextPoll(handledMessages == 0 ? options.delay().toMillis() : 0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            scheduleNextPoll(options.delay().toMillis());
        }
    }

    private int pollAndCountMessages() {
        if (listener instanceof MessageCountingSqsMessageListener countingListener) {
            return countingListener.pollAndCountMessages();
        }

        listener.poll();

        return 0;
    }

    private void awaitTermination(ScheduledExecutorService scheduler) {
        try {
            if (!scheduler.awaitTermination(options.shutdownTimeout().toMillis(), MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            running.set(false);
        }
    }

    @Override
    public void close() {
        stop();
    }
}
