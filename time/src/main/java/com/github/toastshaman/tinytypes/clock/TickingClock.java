package com.github.toastshaman.tinytypes.clock;

import static java.time.ZoneOffset.UTC;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class TickingClock extends Clock {

    private final Ticker ticker;

    private final ZoneId zoneId;

    public TickingClock(Ticker ticker) {
        this(ticker, UTC);
    }

    public TickingClock(Ticker ticker, ZoneId zoneId) {
        this.ticker = Objects.requireNonNull(ticker);
        this.zoneId = Objects.requireNonNull(zoneId);
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new TickingClock(ticker, zone);
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(TimeUnit.NANOSECONDS.toMillis(ticker.read()));
    }
}
