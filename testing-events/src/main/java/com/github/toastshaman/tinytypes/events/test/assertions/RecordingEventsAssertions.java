package com.github.toastshaman.tinytypes.events.test.assertions;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.RecordingEvents;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;

@SuppressWarnings("UnusedReturnValue")
public final class RecordingEventsAssertions extends AbstractAssert<RecordingEventsAssertions, RecordingEvents> {

    private RecordingEventsAssertions(RecordingEvents events) {
        super(events, RecordingEventsAssertions.class);
    }

    public static RecordingEventsAssertions assertThatEvents(RecordingEvents events) {
        return new RecordingEventsAssertions(events);
    }

    public <T extends Event> ListAssert<Event> findInstanceOf(Class<T> type) {
        return assertThat(actual.filterInstanceOf(type));
    }

    public <T extends Event> ListAssert<Event> contains(Class<T> type) {
        return findInstanceOf(type).isNotEmpty();
    }

    public <T extends Event> ObjectAssert<Event> containsSingle(Class<T> type) {
        var events = actual.filterInstanceOf(type);
        if (events.isEmpty()) {
            failWithMessage("no event found matching %s".formatted(type.getSimpleName()));
        }
        if (events.size() > 1) {
            failWithMessage("more than one event found matching %s: %s".formatted(type.getSimpleName(), events));
        }
        return assertThat(events.getFirst());
    }

    public <T extends Event> ListAssert<Event> doesNotContain(Class<T> type) {
        return findInstanceOf(type).hasSize(0);
    }

    public void hasSize(int i) {
        assertThat(actual.captured).as("events").hasSize(i);
    }

    public void isEmpty() {
        assertThat(actual.captured).as("events").isEmpty();
    }

    public void hasSizeGreaterThanOrEqualTo(int i) {
        assertThat(actual.captured).as("events").hasSizeGreaterThanOrEqualTo(i);
    }
}
