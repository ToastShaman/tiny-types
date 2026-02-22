package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.services.sqs.model.Message;

public interface SqsMessagesHandler extends Consumer<List<Message>> {

    static <T> SqsMessagesHandler forEach(Function<Message, T> handler) {
        return messages -> messages.forEach(handler::apply);
    }

    static SqsMessagesHandler of(Function<List<Message>, Void> handler) {
        return handler::apply;
    }
}
