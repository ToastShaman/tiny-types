package com.github.toastshaman.tinytypes.events;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

public final class RecordingEvents implements Events {

    public final LinkedBlockingQueue<Event> captured;

    public RecordingEvents() {
        this(500);
    }

    public RecordingEvents(int maxSize) {
        this.captured = new LinkedBlockingQueue<>(maxSize);
    }

    @Override
    public void record(Event event) {
        if (captured.remainingCapacity() <= 0) {
            captured.clear();
        }
        captured.add(event);
    }

    public <T extends Event> List<Event> filterInstanceOf(Class<T> type) {
        Predicate<Event> isMetadataMatching = it -> it instanceof MetadataEvent m && type.isInstance(m.event());
        Predicate<Event> isEventMatching = type::isInstance;

        return captured.stream().filter(isMetadataMatching.or(isEventMatching)).toList();
    }
}
