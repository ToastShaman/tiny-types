package com.github.toastshaman.tinytypes.events;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class Slf4jEventLoggerTest {

    @Test
    void record_should_write_the_event_toString_to_the_consumer() {
        var messages = new ArrayList<String>();
        var logger = new Slf4jEventLogger(messages::add);
        var event = new MyTestEvent("test-id-123");

        logger.record(event);

        assertThat(messages).containsExactly(event.toString());
    }

    @Test
    void accept_should_write_the_string_to_the_consumer() {
        var messages = new ArrayList<String>();
        var logger = new Slf4jEventLogger(messages::add);
        var message = "my raw log message";

        logger.accept(message);

        assertThat(messages).containsExactly(message);
    }

    private record MyTestEvent(String id) implements Event {}
}
