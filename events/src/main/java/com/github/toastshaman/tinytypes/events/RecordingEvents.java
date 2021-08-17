package com.github.toastshaman.tinytypes.events;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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

    @SuppressWarnings("unchecked")
    public <T> List<T> filterInstanceOf(Class<T> type) {
        return captured.stream().filter(type::isInstance).map(it -> (T) it).toList();
    }
}
