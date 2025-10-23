package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.PollingSqsEvents.Failed;
import static com.github.toastshaman.tinytypes.aws.sqs.PollingSqsEvents.Success;

import com.github.toastshaman.tinytypes.events.Events;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public record PollingSqsMessageListener(
        QueueUrl queueUrl, SqsClient sqs, Events events, Options options, SqsMessageHandler handler)
        implements SqsMessageListener {

    @RecordBuilder
    @RecordBuilder.Options(addClassRetainedGenerated = true)
    public record Options(int maxNumberOfMessages, int waitTimeSeconds) {
        public Options {
            if (maxNumberOfMessages < 1 || maxNumberOfMessages > 10) {
                throw new IllegalArgumentException("maxNumberOfMessages must be between 1 and 10");
            }

            if (waitTimeSeconds < 0) {
                throw new IllegalArgumentException("waitTimeSeconds must be greater than 0");
            }
        }
    }

    public PollingSqsMessageListener {
        Objects.requireNonNull(sqs, "sqs client must not be null");
        Objects.requireNonNull(queueUrl, "queue url must not be null");
        Objects.requireNonNull(events, "events must not be null");
        Objects.requireNonNull(options, "options must not be null");
        Objects.requireNonNull(handler, "handler must not be null");
    }

    @Override
    public void poll() {
        try {
            var messages = receiveMessages();

            if (messages.isEmpty()) {
                return;
            }

            handler.handle(messages);

            deleteMessages(messages);

            events.record(Success(queueUrl, messages));
        } catch (Exception exception) {
            events.record(Failed(queueUrl, exception));
        }
    }

    private List<Message> receiveMessages() {
        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl.toString())
                .maxNumberOfMessages(options.maxNumberOfMessages)
                .waitTimeSeconds(options.waitTimeSeconds)
                .messageAttributeNames("All")
                .build();

        var response = sqs.receiveMessage(request);

        return response.messages();
    }

    private void deleteMessages(List<Message> messages) {
        var entries = messages.stream()
                .map(message -> DeleteMessageBatchRequestEntry.builder()
                        .id(message.messageId())
                        .receiptHandle(message.receiptHandle())
                        .build())
                .toList();

        sqs.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl.toString())
                .entries(entries)
                .build());
    }
}
