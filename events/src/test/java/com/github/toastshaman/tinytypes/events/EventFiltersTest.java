package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.*;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddEventName;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddServiceName;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddTimestamp;
import static com.github.toastshaman.tinytypes.events.EventFilters.is;
import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;
import static java.time.Instant.EPOCH;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EventFiltersTest {

    private final RecordingEvents recording = new RecordingEvents();

    @Test
    void can_chain_multiple_filters() {
        var events = AddEventName.then(AddServiceName("my-service"))
                .then(AddTimestamp(Clock.fixed(EPOCH, UTC)))
                .then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("name", "MyEvent")
                .containsEntry("service", "my-service")
                .containsEntry("timestamp", EPOCH);
    }

    @Test
    void can_add_custom_filter() {
        var events = EventFilter.of(next -> event -> next.record(event.addMetadata("hello", "world")))
                .then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("hello", "world");
    }

    @Test
    void can_reject_events() {
        var events = EventFilters.Reject(is(MyEvent.class)).then(recording);

        events.record(MyEvent.random());
        events.record(MyErrorEvent.random());

        assertThatEvents(recording).doesNotContain(MyEvent.class);
        assertThatEvents(recording).containsSingle(MyErrorEvent.class);
    }

    @Test
    void can_reject_events_metadata_events() {
        var events = EventFilters.Reject(is(MyEvent.class)).then(recording);

        events.record(MyEvent.random());

        assertThatEvents(recording).doesNotContain(MyEvent.class);
    }

    @Test
    void can_reject_event_categories() {
        var events = EventFilters.Reject(INFO, ERROR).then(recording);

        events.record(MyEvent.random());
        events.record(MyErrorEvent.random());

        assertThatEvents(recording).doesNotContain(MyEvent.class);
        assertThatEvents(recording).doesNotContain(MyErrorEvent.class);
    }

    @Test
    void can_allow_event_categories() {
        var events = EventFilters.Accept(INFO, ERROR).then(recording);

        events.record(MyEvent.random());
        events.record(MyErrorEvent.random());

        assertThatEvents(recording).containsSingle(MyEvent.class);
        assertThatEvents(recording).containsSingle(MyErrorEvent.class);
    }

    private record MyEvent(UUID id) implements Event {
        public static MyEvent random() {
            return new MyEvent(UUID.randomUUID());
        }
    }

    private record MyErrorEvent(UUID id) implements Event {
        @Override
        public EventCategory category() {
            return ERROR;
        }

        public static MyErrorEvent random() {
            return new MyErrorEvent(UUID.randomUUID());
        }
    }
}
