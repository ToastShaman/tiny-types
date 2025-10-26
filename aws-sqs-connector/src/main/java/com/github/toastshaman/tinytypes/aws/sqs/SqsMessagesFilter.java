package com.github.toastshaman.tinytypes.aws.sqs;

public interface SqsMessagesFilter {

    SqsMessagesHandler filter(SqsMessagesHandler handler);

    default SqsMessagesFilter andThen(SqsMessagesFilter next) {
        return events -> filter(next.filter(events));
    }

    default SqsMessagesHandler andThen(SqsMessagesHandler next) {
        return messages -> filter(next).handle(messages);
    }
}
