package com.github.toastshaman.tinytypes.events;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RecordingEventsTest {

    RecordingEvents recordingEvents = new RecordingEvents();

    @Test
    void returns_true_if_all_events_match() {
        recordingEvents.record(new MyFirstEvent());

        assertThat(recordingEvents.allMatch(MyFirstEvent.class)).isTrue();
        assertThat(recordingEvents.allMatch(MySecondEvent.class)).isFalse();
    }

    @Test
    void returns_true_if_none_events_match() {
        recordingEvents.record(new MyFirstEvent());

        assertThat(recordingEvents.noneMatch(MySecondEvent.class)).isTrue();
    }

    @Test
    void returns_true_if_any_events_match() {
        recordingEvents.record(new MyFirstEvent());
        recordingEvents.record(new MySecondEvent());

        assertThat(recordingEvents.anyMatch(MySecondEvent.class)).isTrue();
    }

    @Test
    void can_handle_metadata_events() {
        var events = EventFilters.AddTimestamp(Clock.systemUTC()).then(recordingEvents);

        events.record(new MyFirstEvent());

        assertThat(recordingEvents.allMatch(MyFirstEvent.class)).isTrue();
        assertThat(recordingEvents.allMatch(MySecondEvent.class)).isFalse();
        assertThat(recordingEvents.anyMatch(MyFirstEvent.class)).isTrue();
        assertThat(recordingEvents.anyMatch(MySecondEvent.class)).isFalse();
        assertThat(recordingEvents.noneMatch(MySecondEvent.class)).isTrue();
        assertThat(recordingEvents.noneMatch(MyFirstEvent.class)).isFalse();
    }

    private record MyFirstEvent() implements Event {}

    private record MySecondEvent() implements Event {}
}
