package com.github.toastshaman.tinytypes.aws.sqs;

import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.services.sqs.model.Message;

public interface SqsMessagesHandler extends Consumer<List<Message>> {}
