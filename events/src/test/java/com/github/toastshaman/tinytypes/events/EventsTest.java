package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class EventsTest {

    @Test
    void can_append() {
        var recordingEvents = new RecordingEvents();
        var events = recordingEvents.appendNext(recordingEvents);
        events.record(new MyEvent("Hello"));

        assertThatEvents(recordingEvents).contains(MyEvent.class, 2);
    }

    private record MyEvent(String value) implements Event {}
}
