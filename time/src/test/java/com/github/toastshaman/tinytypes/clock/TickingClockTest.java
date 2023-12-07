package com.github.toastshaman.tinytypes.clock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TickingClockTest {

    @Test
    void ticking_clock_advances() {
        var ticker =
                new FakeTicker().at(Instant.parse("2023-12-06T10:00:00.000Z")).setAutoIncrementStep(5, SECONDS);

        var clock = new TickingClock(ticker);

        assertThat(clock.instant()).isEqualTo("2023-12-06T10:00:00.000Z");
        assertThat(clock.instant()).isEqualTo("2023-12-06T10:00:05.000Z");
        assertThat(clock.instant()).isEqualTo("2023-12-06T10:00:10.000Z");
    }

    @Test
    void ticking_clock_can_be_controlled() {
        var ticker = new FakeTicker().at(Instant.parse("2023-12-06T10:00:00.000Z"));

        var clock = new TickingClock(ticker);

        assertThat(clock.instant()).isEqualTo("2023-12-06T10:00:00.000Z");
        assertThat(clock.instant()).isEqualTo("2023-12-06T10:00:00.000Z");

        ticker.advance(Duration.ofSeconds(10));
        assertThat(clock.instant()).isEqualTo("2023-12-06T10:00:10.000Z");
    }
}
