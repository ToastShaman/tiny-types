package com.github.toastshaman.tinytypes.aws.sqs;

import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.DelegatingSqsMessageHandler;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.MeasuringSqsMessageFilter;
import static com.github.toastshaman.tinytypes.aws.sqs.SqsMessageFilters.RetryingSqsMessageFilter;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.github.toastshaman.tinytypes.events.Events;
import com.github.toastshaman.tinytypes.events.PrintStreamEventLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.datafaker.Faker;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DelegatingSqsMessageHandlerTest {

    Faker faker = new Faker(new Random(578));

    Events events = new PrintStreamEventLogger(System.out);

    @Test
    void can_chain_multiple_filters_together() {
        var captured = new ArrayList<String>();

        var chain = MeasuringSqsMessageFilter(events)
                .andThen(RetryingSqsMessageFilter(builder -> builder.withMaxRetries(3)))
                .andThen(DelegatingSqsMessageHandler(((SqsMessageHandler<String>) Message::body)
                        .andThen(JSONObject::new)
                        .andThen(it -> it.getString("message"))
                        .andThen(captured::add)));

        var messages = someMessages();

        chain.handle(messages);

        assertThat(captured).containsExactly("Molly", "Max", "Daisy");
    }

    List<Message> someMessages() {
        return faker.collection(this::aMessage).len(3, 5).generate();
    }

    Message aMessage() {
        return Message.builder()
                .messageId(faker.internet().uuid())
                .body(new JSONObject().put("message", faker.cat().name()).toString())
                .build();
    }
}
