package com.github.toastshaman.tinytypes.clock;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class FakeTickerTest {

    @Test
    void is_ticking() {
        var ticker = new FakeTicker().setAutoIncrementStep(Duration.ofSeconds(1));

        assertThat(ticker.read()).isEqualTo(0);
        assertThat(ticker.read()).isEqualTo(1000000000);
        assertThat(ticker.read()).isEqualTo(2000000000);
    }
}
