package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import software.amazon.awssdk.services.sqs.model.Message;

public interface SqsMessageHandler {

    void handle(List<Message> messages);
}
