package com.github.toastshaman.tinytypes.events;

import java.io.PrintStream;
import java.util.Objects;
import java.util.function.Consumer;

public final class PrintStreamEventLogger implements Events, AutoCloseable, Consumer<String> {

    private final PrintStream writer;

    public PrintStreamEventLogger() {
        this(System.out);
    }

    public PrintStreamEventLogger(PrintStream writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void record(Event event) {
        accept(event.toString());
    }

    @Override
    public void accept(String text) {
        writer.println(text);
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
