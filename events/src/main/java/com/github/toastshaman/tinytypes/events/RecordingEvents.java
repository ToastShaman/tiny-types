package com.github.toastshaman.tinytypes.events;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public final class RecordingEvents implements Events {

    public final LinkedBlockingQueue<Event> captured;

    private final ReentrantLock lock = new ReentrantLock();

    public RecordingEvents() {
        this(500);
    }

    public RecordingEvents(int maxSize) {
        if (maxSize <= 0) throw new IllegalArgumentException("max size must be greater than 0");
        this.captured = new LinkedBlockingQueue<>(maxSize);
    }

    @Override
    public void record(Event event) {
        lock.lock();
        try {
            if (captured.remainingCapacity() <= 0) {
                captured.clear();
            }
            captured.add(event);
        } finally {
            lock.unlock();
        }
    }

    public <T extends Event> List<Event> filterInstanceOf(Class<T> type) {
        return captured.stream()
                .filter(getMetadataMatching(type).or(type::isInstance))
                .toList();
    }

    public <T extends Event> boolean anyMatch(Class<T> type) {
        return captured.stream().anyMatch(getMetadataMatching(type).or(type::isInstance));
    }

    public <T extends Event> boolean allMatch(Class<T> type) {
        return captured.stream().allMatch(getMetadataMatching(type).or(type::isInstance));
    }

    public <T extends Event> boolean noneMatch(Class<T> type) {
        return captured.stream().noneMatch(getMetadataMatching(type).or(type::isInstance));
    }

    private static <T extends Event> Predicate<Event> getMetadataMatching(Class<T> type) {
        return it -> it instanceof MetadataEvent m && type.isInstance(m.event());
    }
}
