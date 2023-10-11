package com.github.toastshaman.tinytypes.events;

import java.io.PrintStream;
import java.util.Objects;

public final class PrintStreamEventLogger implements Events, AutoCloseable {

    private final PrintStream writer;

    public PrintStreamEventLogger() {
        this(System.out);
    }

    public PrintStreamEventLogger(PrintStream writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void record(Event event) {
        writer.println(event.toString());
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
