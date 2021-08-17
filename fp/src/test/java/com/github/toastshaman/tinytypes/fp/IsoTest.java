package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class IsoTest {

    @Test
    void can_convert() {
        var iso = Iso.<String, Integer>of(Integer::valueOf, String::valueOf);

        assertThat(iso.get("100")).isEqualTo(100);
        assertThat(iso.reverseGet(100)).isEqualTo("100");
    }
}
