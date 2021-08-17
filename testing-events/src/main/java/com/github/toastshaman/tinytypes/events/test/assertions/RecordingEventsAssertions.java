package com.github.toastshaman.tinytypes.events.test.assertions;

import com.github.toastshaman.tinytypes.events.RecordingEvents;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;

public final class RecordingEventsAssertions extends AbstractAssert<RecordingEventsAssertions, RecordingEvents> {

    private RecordingEventsAssertions(RecordingEvents events) {
        super(events, RecordingEventsAssertions.class);
    }

    public static RecordingEventsAssertions assertThatEvents(RecordingEvents events) {
        return new RecordingEventsAssertions(events);
    }

    public <T> ListAssert<T> filterInstanceOf(Class<T> type) {
        return Assertions.assertThat(actual.filterInstanceOf(type));
    }

    public <T> ListAssert<T> contains(Class<T> type) {
        return filterInstanceOf(type).isNotEmpty();
    }

    public <T> ObjectAssert<T> containsSingle(Class<T> type) {
        return filterInstanceOf(type).hasSize(1).first();
    }

    public <T> ObjectAssert<T> contains(Class<T> type, int howMany) {
        return filterInstanceOf(type).hasSize(howMany).first();
    }

    public <T> ListAssert<T> doesNotContain(Class<T> type) {
        return filterInstanceOf(type).hasSize(0);
    }
}
