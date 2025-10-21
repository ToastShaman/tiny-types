package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.PollingSqsEvents.Failed;
import static com.github.toastshaman.tinytypes.aws.sqs.PollingSqsEvents.Retry;
import static com.github.toastshaman.tinytypes.aws.sqs.PollingSqsEvents.Success;

import com.github.toastshaman.tinytypes.events.Events;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public final class RetryingSqsMessageListener implements SqsMessageListener {

    private static final Logger log = LoggerFactory.getLogger(RetryingSqsMessageListener.class);

    private final QueueUrl queueUrl;

    private final SqsClient sqs;

    private final Events events;

    private final RetryPolicy<Void> retryPolicy;

    public RetryingSqsMessageListener(QueueUrl queueUrl, SqsClient sqs, Events events) {
        this.sqs = Objects.requireNonNull(sqs);
        this.queueUrl = Objects.requireNonNull(queueUrl);
        this.events = Objects.requireNonNull(events);
        this.retryPolicy = RetryPolicy.<Void>builder()
                .withMaxRetries(3)
                .withDelay(Duration.ofSeconds(1))
                .onFailedAttempt(it -> events.record(Failed(queueUrl, it.getLastException())))
                .onRetry(it -> events.record(Retry(queueUrl)))
                .build();
    }

    @Override
    public void poll(SqsMessageHandler handler) {
        Objects.requireNonNull(handler);

        try {
            var messages = receiveMessages();
            if (messages.isEmpty()) {
                return;
            }

            Failsafe.with(retryPolicy)
                    .onSuccess(_ -> deleteAll(messages))
                    .onFailure(_ -> deleteAll(messages))
                    .run(() -> handler.handle(messages));

            events.record(Success(queueUrl, messages));
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            events.record(Failed(queueUrl, exception));
        }
    }

    private List<Message> receiveMessages() {
        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl.toString())
                .maxNumberOfMessages(10)
                .messageAttributeNames("All")
                .build();

        var response = sqs.receiveMessage(request);

        return response.messages();
    }

    private void deleteAll(List<Message> messages) {
        var entries = messages.stream()
                .map(RetryingSqsMessageListener::DeleteMessageBatchRequestEntry)
                .toList();

        sqs.deleteMessageBatch(DeleteMessageBatchRequest(queueUrl, entries));
    }

    private static DeleteMessageBatchRequestEntry DeleteMessageBatchRequestEntry(Message message) {
        return DeleteMessageBatchRequestEntry.builder()
                .id(message.messageId())
                .receiptHandle(message.receiptHandle())
                .build();
    }

    private static DeleteMessageBatchRequest DeleteMessageBatchRequest(
            QueueUrl queueUrl, Collection<DeleteMessageBatchRequestEntry> entries) {
        return DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl.toString())
                .entries(entries)
                .build();
    }
}
