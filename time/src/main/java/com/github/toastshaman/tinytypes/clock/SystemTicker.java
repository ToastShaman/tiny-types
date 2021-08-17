package com.github.toastshaman.tinytypes.clock;

public final class SystemTicker implements Ticker {

    public static final SystemTicker INSTANCE = new SystemTicker();

    public SystemTicker() {}

    @Override
    public long read() {
        return System.nanoTime();
    }

    public static SystemTicker systemNanoTime() {
        return INSTANCE;
    }
}
