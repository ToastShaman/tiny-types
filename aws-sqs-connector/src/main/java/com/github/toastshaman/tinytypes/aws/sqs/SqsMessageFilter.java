package com.github.toastshaman.tinytypes.aws.sqs;

public interface SqsMessageFilter {

    SqsMessageHandler filter(SqsMessageHandler handler);

    default SqsMessageFilter andThen(SqsMessageFilter next) {
        return events -> filter(next.filter(events));
    }

    default SqsMessageHandler andThen(SqsMessageHandler next) {
        return messages -> filter(next).handle(messages);
    }
}
