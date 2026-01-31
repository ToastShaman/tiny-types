package com.github.toastshaman.tinytypes.aws.sqs;

public interface SqsMessagesFilter {

    SqsMessagesHandler filter(SqsMessagesHandler next);

    default SqsMessagesFilter andThen(SqsMessagesFilter next) {
        return messages -> filter(next.filter(messages));
    }

    default SqsMessagesHandler andThen(SqsMessagesHandler next) {
        return messages -> filter(next).accept(messages);
    }
}
