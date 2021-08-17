package com.github.toastshaman.tinytypes.events;

import java.io.PrintStream;
import java.util.Objects;
import java.util.function.Function;
import org.json.JSONObject;

public final class PrintStreamEventLogger implements Events, AutoCloseable {

    private final PrintStream writer;

    private final Function<Event, JSONObject> mapper;

    public PrintStreamEventLogger(Function<Event, JSONObject> mapper) {
        this(System.out, mapper);
    }

    public PrintStreamEventLogger(PrintStream writer, Function<Event, JSONObject> mapper) {
        this.writer = Objects.requireNonNull(writer);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public void record(Event event) {
        writer.println(mapper.apply(event).toString());
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
