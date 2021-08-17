package com.github.toastshaman.tinytypes.obfuscators;

import static com.github.toastshaman.tinytypes.validation.Validator.AlwaysValid;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.values.NonBlankStringValue;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ObfuscateTest {

    @ParameterizedTest
    @ValueSource(strings = {"18362789", "foo", "foobar"})
    void hides_all(String value) {
        assertThat(MySecret.of(value, Obfuscate.fully()).toString()).isEqualTo("********");
    }

    @ParameterizedTest
    @CsvSource(value = {"18362789,********789", "foo,********", "foobar,********bar"})
    void keeps_last_three(String value, String expected) {
        assertThat(MySecret.of(value, Obfuscate.keepLast(3)).toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"18362789,183********", "foo,********", "foobar,foo********"})
    void keeps_first_three(String value, String expected) {
        assertThat(MySecret.of(value, Obfuscate.keepFirst(3)).toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void first_handles_negative_values(int keep) {
        assertThat(MySecret.of("foobar", Obfuscate.keepFirst(keep)).toString()).isEqualTo("********");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void last_handles_negative_values(int keep) {
        assertThat(MySecret.of("foobar", Obfuscate.keepLast(keep)).toString()).isEqualTo("********");
    }

    private static class MySecret extends NonBlankStringValue {
        public MySecret(String value, Function<String, String> showFn) {
            super(value, AlwaysValid(), showFn);
        }

        public static MySecret of(String value, Function<String, String> showFn) {
            return new MySecret(value, showFn);
        }
    }
}
