package com.github.toastshaman.tinytypes.aws.sqs;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.EventCategory;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.services.sqs.model.Message;

public sealed interface PollingSqsEvents extends Event
        permits PollingSqsEvents.PollingFailed, PollingSqsEvents.PollingSuccess, PollingSqsEvents.PollingRetry {

    static PollingFailed Failed(QueueUrl queueUrl, Throwable throwable) {
        return new PollingFailed(queueUrl, throwable, throwable.getMessage());
    }

    static PollingSuccess Success(QueueUrl queueUrl, List<Message> messages) {
        return new PollingSuccess(
                queueUrl,
                messages.stream().map(Message::messageId).map(MessageId::of).toList());
    }

    static PollingRetry Retry(QueueUrl queueUrl) {
        return new PollingRetry(queueUrl);
    }

    @Override
    default EventCategory category() {
        return EventCategory.INFO;
    }

    @RecordBuilder
    @RecordBuilder.Options(addClassRetainedGenerated = true)
    record PollingSuccess(QueueUrl queueUrl, List<MessageId> messages) implements PollingSqsEvents {

        public PollingSuccess {
            Objects.requireNonNull(queueUrl);
            Objects.requireNonNull(messages);
        }
    }

    @RecordBuilder
    @RecordBuilder.Options(addClassRetainedGenerated = true)
    record PollingFailed(QueueUrl queueUrl, Throwable throwable, String message) implements PollingSqsEvents {

        public PollingFailed {
            Objects.requireNonNull(queueUrl);
            Objects.requireNonNull(throwable);
            Objects.requireNonNull(message);
        }
    }

    @RecordBuilder
    @RecordBuilder.Options(addClassRetainedGenerated = true)
    record PollingRetry(QueueUrl queueUrl) implements PollingSqsEvents {

        public PollingRetry {
            Objects.requireNonNull(queueUrl);
        }
    }
}
