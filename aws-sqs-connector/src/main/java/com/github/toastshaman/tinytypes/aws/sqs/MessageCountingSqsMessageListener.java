package com.github.toastshaman.tinytypes.aws.sqs;

public interface MessageCountingSqsMessageListener extends SqsMessageListener {

    int pollAndCountMessages();

    @Override
    default void poll() {
        pollAndCountMessages();
    }
}
