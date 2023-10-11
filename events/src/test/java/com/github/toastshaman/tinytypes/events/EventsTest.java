package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class EventsTest {

    private final RecordingEvents recordingEvents = new RecordingEvents();

    private final PrintStreamEventLogger print = new PrintStreamEventLogger();

    @Test
    void can_chain_multiple_events() {
        var events = recordingEvents.and(print);

        events.record(new MyEvent("Hello"));

        assertThatEvents(recordingEvents).contains(MyEvent.class);
    }

    private record MyEvent(String value) implements Event {}
}
