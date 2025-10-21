package com.github.toastshaman.tinytypes.aws.sqs;

public interface SqsMessageListener {

    void poll(SqsMessageHandler handler);
}
