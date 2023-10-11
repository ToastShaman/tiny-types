package com.github.toastshaman.tinytypes.events;

import static com.github.toastshaman.tinytypes.events.EventCategory.*;
import static com.github.toastshaman.tinytypes.events.EventCategory.INFO;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddEventName;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddServiceName;
import static com.github.toastshaman.tinytypes.events.EventFilters.AddTimestamp;
import static com.github.toastshaman.tinytypes.events.test.assertions.RecordingEventsAssertions.assertThatEvents;
import static java.time.Instant.EPOCH;
import static java.time.ZoneOffset.UTC;

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
                .containsSingleSatisfying(
                        MyEvent.class,
                        it -> it.hasMetadataSatisfying(data -> {
                            data.containsEntry("service", "my-service");
                            data.containsEntry("timestamp", EPOCH);
                            data.containsEntry("name", "MyEvent");
                        }));
    }

    @Test
    void can_add_custom_filter() {
        var customFilter = EventFilter.of(next ->
                event -> next.record(event.addMetadata("category", event instanceof Event.Error ? ERROR : INFO)));

        var events = customFilter.then(printing.and(recording));

        events.record(MyEvent.random());

        assertThatEvents(recording)
                .containsSingleSatisfying(
                        MyEvent.class, it -> it.hasMetadataSatisfying(data -> data.containsEntry("category", INFO)));
    }
}
