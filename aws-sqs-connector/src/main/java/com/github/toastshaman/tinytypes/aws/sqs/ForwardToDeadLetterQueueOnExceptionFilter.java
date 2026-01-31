package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

@SuppressWarnings("ClassCanBeRecord")
public final class ForwardToDeadLetterQueueOnExceptionFilter implements SqsMessagesFilter {

    private final DeadLetterQueueUrl queueUrl;

    private final SqsClient sqs;

    private final Predicate<Exception> filter;

    public ForwardToDeadLetterQueueOnExceptionFilter(DeadLetterQueueUrl queueUrl, SqsClient sqs) {
        this(queueUrl, sqs, e -> true);
    }

    public ForwardToDeadLetterQueueOnExceptionFilter(
            DeadLetterQueueUrl queueUrl, SqsClient sqs, Predicate<Exception> filter) {
        this.queueUrl = Objects.requireNonNull(queueUrl, "queueUrl must not be null");
        this.sqs = Objects.requireNonNull(sqs, "sqs client must not be null");
        this.filter = Objects.requireNonNull(filter, "filter must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        return messages -> {
            try {
                next.accept(messages);
            } catch (Exception e) {
                if (!filter.test(e)) {
                    throw e;
                }

                var entries = messages.stream()
                        .map(ForwardToDeadLetterQueueOnExceptionFilter::entryFrom)
                        .toList();

                var build = SendMessageBatchRequest.builder()
                        .queueUrl(queueUrl.asString())
                        .entries(entries)
                        .build();

                sqs.sendMessageBatch(build);
            }
        };
    }

    private static SendMessageBatchRequestEntry entryFrom(Message message) {
        var attributes = message.messageAttributes();

        var request = SendMessageBatchRequestEntry.builder()
                .id(message.messageId())
                .messageBody(message.body())
                .messageAttributes(attributes);

        Optional.ofNullable(attributes.get("MessageGroupId"))
                .map(MessageAttributeValue::stringValue)
                .ifPresent(request::messageGroupId);

        Optional.ofNullable(attributes.get("MessageDeduplicationId"))
                .map(MessageAttributeValue::stringValue)
                .ifPresent(request::messageDeduplicationId);

        return request.build();
    }

    public static Predicate<Exception> isInstanceOfOrHasCause(Class<? extends Throwable> targetClass) {
        return throwable -> {
            if (throwable == null || targetClass == null) {
                return false;
            }

            if (targetClass.isInstance(throwable)) {
                return true;
            }

            Throwable cause = throwable.getCause();
            while (cause != null) {
                if (targetClass.isInstance(cause)) {
                    return true;
                }
                cause = cause.getCause();
            }

            return false;
        };
    }
}
