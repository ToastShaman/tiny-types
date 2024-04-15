package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventFilters.AddEventName;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddServiceName;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddTimestamp;
import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;
import static java.time.Instant.EPOCH;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import java.time.Clock;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class EventFilterTest {

    private final RecordingEvents recording = new RecordingEvents();

    private final PrintStreamEventLogger printing = new PrintStreamEventLogger();

    @Test
    void can_chain_multiple_filters() {
        var events = AddEventName.then(AddServiceName("my-service"))
                .then(AddTimestamp(Clock.fixed(EPOCH, UTC)))
                .then(printing.and(recording));

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("service", "my-service")
                .containsEntry("timestamp", EPOCH)
                .containsEntry("name", "MyEvent");
    }

    @Test
    void can_add_custom_filter() {
        var events = EventFilter.of(next -> event -> next.record(event.addMetadata("hello", "world")))
                .then(printing.and(recording));

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingle(MyEvent.class)
                .asInstanceOf(type(MetadataEvent.class))
                .extracting(MetadataEvent::metadata, as(MAP))
                .containsEntry("hello", "world");
    }
}
