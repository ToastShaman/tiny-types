package com.github.toastshaman.tinytypes.clock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class FakeTicker implements Ticker {

    private final AtomicLong nanos = new AtomicLong();

    private volatile long autoIncrementStepNanos;

    @Override
    public long read() {
        return nanos.getAndAdd(autoIncrementStepNanos);
    }

    public FakeTicker advance(long time, TimeUnit timeUnit) {
        return advance(timeUnit.toNanos(time));
    }

    public FakeTicker advance(long nanoseconds) {
        nanos.addAndGet(nanoseconds);
        return this;
    }

    public FakeTicker advance(Duration duration) {
        return advance(duration.toNanos());
    }

    public FakeTicker setAutoIncrementStep(long autoIncrementStep, TimeUnit timeUnit) {
        if (autoIncrementStep <= 0) {
            throw new IllegalArgumentException("May not auto-increment by a negative amount");
        }
        this.autoIncrementStepNanos = timeUnit.toNanos(autoIncrementStep);
        return this;
    }

    public FakeTicker setAutoIncrementStep(Duration autoIncrementStep) {
        return setAutoIncrementStep(autoIncrementStep.toNanos(), TimeUnit.NANOSECONDS);
    }
}
