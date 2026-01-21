package com.github.toastshaman.tinytypes.aws.sqs;

import software.amazon.awssdk.services.sqs.model.Message;

public interface VoidSqsMessageHandler extends SqsMessageHandler<Message, Void> {

    void accept(Message message);

    @Override
    default Void handle(Message message) {
        accept(message);
        return null;
    }
}
