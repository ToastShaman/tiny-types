package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import software.amazon.awssdk.services.sqs.model.Message;

@SuppressWarnings("ClassCanBeRecord")
public final class ParallelSqsMessageHandler<T> implements SqsMessagesHandler {

    private final SqsMessageHandler<Message, T> handler;

    public ParallelSqsMessageHandler(SqsMessageHandler<Message, T> handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void handle(List<Message> messages) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = messages.stream()
                    .map(message -> CompletableFuture.runAsync(() -> handler.handle(message), executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }
    }
}
