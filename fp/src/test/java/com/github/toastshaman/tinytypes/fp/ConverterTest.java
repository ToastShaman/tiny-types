package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ConverterTest {

    @Test
    void can_convert() {
        var converted = Converter.<String, Integer>of(Integer::valueOf).convert("100");

        assertThat(converted).isEqualTo(100);
    }
}
