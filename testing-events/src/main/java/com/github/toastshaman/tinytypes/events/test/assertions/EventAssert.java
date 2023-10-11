package com.github.toastshaman.tinytypes.events.test.assertions;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.events.Event;
import com.github.toastshaman.tinytypes.events.MetadataEvent;
import java.util.function.Consumer;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.ObjectAssert;

@SuppressWarnings("UnusedReturnValue")
public final class EventAssert extends ObjectAssert<Event> {

    private EventAssert(Event event) {
        super(event);
    }

    public static EventAssert assertThatEvent(Event events) {
        return new EventAssert(events);
    }

    public MapAssert<String, Object> hasMetadataSatisfying(Consumer<MapAssert<String, Object>> assertions) {
        isInstanceOf(MetadataEvent.class);
        return assertThat(((MetadataEvent) actual).metadata()).satisfies(it -> assertions.accept(assertThat(it)));
    }
}
