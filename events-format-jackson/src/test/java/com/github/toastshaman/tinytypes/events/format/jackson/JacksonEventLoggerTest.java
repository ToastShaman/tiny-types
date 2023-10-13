package com.github.toastshaman.tinytypes.events.format.jackson;

import static com.github.toastshaman.tinytypes.events.EventCategory.INFO;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class JacksonEventLoggerTest {

    @Test
    void can_log_events() {
        var out = new StringBuilder();
        var events = new JacksonEventLogger(out::append, new ObjectMapper().registerModule(new EventsModule()));

        var event = new MyEvent(42, "Hello World")
                .addMetadata("category", INFO)
                .addMetadata("context", Map.of("nested", Map.of("id", 1)));

        events.record(event);

        assertThatJson(out.toString())
                .isEqualTo(
                        """
        {"event":{"id":42,"name":"Hello World"},"metadata":{"context":{"nested":{"id":1}},"category":"info"}}
        """);
    }
}
