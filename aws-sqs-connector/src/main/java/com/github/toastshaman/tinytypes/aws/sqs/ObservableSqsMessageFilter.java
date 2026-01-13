package com.github.toastshaman.tinytypes.aws.sqs;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class ObservableSqsMessageFilter implements SqsMessagesFilter {

    private final QueueUrl queueUrl;

    private final MeterRegistry meterRegistry;

    public ObservableSqsMessageFilter(QueueUrl queueUrl, MeterRegistry meterRegistry) {
        this.queueUrl = Objects.requireNonNull(queueUrl, "queue url must not be null");
        this.meterRegistry = Objects.requireNonNull(meterRegistry, "meter registry must not be null");
    }

    @Override
    public SqsMessagesHandler filter(SqsMessagesHandler next) {
        var queueUrlAsString = queueUrl.asString();
        var receivedMessages = meterRegistry.counter("sqs.messages.received", "queueUrl", queueUrlAsString);
        var processingErrors = meterRegistry.counter("sqs.message.processing.errors", "queueUrl", queueUrlAsString);
        var processingTimer = meterRegistry.timer("sqs.message.processing.time", "queueUrl", queueUrlAsString);

        return messages -> {
            receivedMessages.increment(messages.size());

            var sample = Timer.start(meterRegistry);
            try {
                next.handle(messages);
            } catch (Exception e) {
                processingErrors.increment(messages.size());
                throw e;
            } finally {
                sample.stop(processingTimer);
            }
        };
    }
}
