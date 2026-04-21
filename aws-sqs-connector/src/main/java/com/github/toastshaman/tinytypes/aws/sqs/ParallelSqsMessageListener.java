package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.ParallelSqsMessageListenerThreadType.PLATFORM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ParallelSqsMessageListener implements SqsMessageListener {

    private final SqsMessageListener delegate;

    private final ParallelSqsMessageListenerOptions options;

    public ParallelSqsMessageListener(SqsMessageListener delegate) {
        this(delegate, new ParallelSqsMessageListenerOptions(1, PLATFORM));
    }

    public ParallelSqsMessageListener(SqsMessageListener delegate, ParallelSqsMessageListenerOptions options) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
    }

    @Override
    public int poll() {
        try (var executor = newExecutor()) {
            var tasks = new ArrayList<CompletableFuture<Integer>>();

            // Start N polling tasks
            for (int i = 0; i < options.threads(); i++) {
                tasks.add(CompletableFuture.supplyAsync(delegate::poll, executor));
            }

            // Wait for all tasks to complete
            CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                    .exceptionally(_ -> null)
                    .join();

            // Collect results
            int totalProcessedMessages = 0;

            var failures = new ArrayList<Exception>();

            for (var task : tasks) {
                try {
                    totalProcessedMessages += task.join();
                } catch (CompletionException e) {
                    failures.add(e);
                }
            }

            if (!failures.isEmpty()) {
                throw new ParallelPollingException(failures);
            }

            return totalProcessedMessages;
        }
    }

    public static class ParallelPollingException extends RuntimeException {
        public final List<Exception> failures;

        public ParallelPollingException(List<Exception> failures) {
            super("Polling failed with %d error(s)".formatted(failures.size()));
            this.failures = List.copyOf(failures);
            failures.forEach(this::addSuppressed);
        }
    }

    private ExecutorService newExecutor() {
        return switch (options.threadType()) {
            case PLATFORM -> Executors.newFixedThreadPool(options.threads());
            case VIRTUAL -> Executors.newVirtualThreadPerTaskExecutor();
        };
    }
}
