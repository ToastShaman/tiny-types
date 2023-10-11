package com.github.toastshaman.tinytypes.events;

import java.util.Objects;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slf4jEventLogger implements Events {

    private static final Logger log = LoggerFactory.getLogger("EVENTS");

    private final Consumer<String> writer;

    public Slf4jEventLogger() {
        this(log::info);
    }

    public Slf4jEventLogger(Consumer<String> writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void record(Event event) {
        writer.accept(event.toString());
    }
}
