package com.github.toastshaman.tinytypes.values;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class SecretStringValueTest {

    @Test
    void obfuscates_secret() {
        assertThat(new MyPassword("secret").toString()).isEqualTo("********");
    }

    private static class MyPassword extends SecretStringValue {
        public MyPassword(String value) {
            super(value);
        }
    }
}
