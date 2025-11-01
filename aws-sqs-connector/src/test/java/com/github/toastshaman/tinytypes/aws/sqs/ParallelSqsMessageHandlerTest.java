package com.github.toastshaman.tinytypes.aws.sqs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

class ParallelSqsMessageHandlerTest {

    @Test
    void handle_should_invoke_handler_for_each_message_in_parallel() {
        var handledMessages = new ConcurrentLinkedDeque<String>();

        var handler = new ParallelSqsMessageHandler<>(
                SqsMessageHandler.fromConsumer(message -> handledMessages.add(message.body())));

        var messages = List.of(
                Message.builder().body("msg1").build(),
                Message.builder().body("msg2").build(),
                Message.builder().body("msg3").build());

        handler.handle(messages);

        assertThat(handledMessages).containsExactlyInAnyOrder("msg1", "msg2", "msg3");
    }
}
