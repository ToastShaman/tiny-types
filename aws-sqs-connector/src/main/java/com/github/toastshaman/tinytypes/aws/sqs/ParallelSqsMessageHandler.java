package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import software.amazon.awssdk.services.sqs.model.Message;

public record ParallelSqsMessageHandler<T>(SqsMessageHandler<T> handler) implements SqsMessagesHandler {

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
