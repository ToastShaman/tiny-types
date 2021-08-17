package com.github.toastshaman.tinytypes.validation;

import static java.time.Instant.EPOCH;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.Tuple3;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ValidatorTest {

    private final Validator<Void> alwaysValid = Validator.of(value -> true, "should not fail");
    private final Validator<Void> alwaysInvalid = Validator.of(value -> false, "validation failed");

    @Nested
    @DisplayName("Validates Numbers")
    class Numbers {
        @Test
        void can_validate() {
            assertThat(Validator.Min(1).isValid(1).get()).isEqualTo(1);
            assertThat(Validator.Min(1L).isValid(1L).get()).isEqualTo(1L);
            assertThat(Validator.Min(1F).isValid(1F).get()).isEqualTo(1F);
            assertThat(Validator.Min(1D).isValid(1D).get()).isEqualTo(1D);

            assertThat(Validator.Min(1).isValid(0).swap().get()).containsExactly("must be greater than or equal to 1");
            assertThat(Validator.Min(1L).isValid(0L).swap().get())
                    .containsExactly("must be greater than or equal to 1");
            assertThat(Validator.Min(1F).isValid(0F).swap().get())
                    .containsExactly("must be greater than or equal to 1.000000");
            assertThat(Validator.Min(1D).isValid(0D).swap().get())
                    .containsExactly("must be greater than or equal to 1.000000");

            assertThat(Validator.Max(1).isValid(1).get()).isEqualTo(1);
            assertThat(Validator.Max(1L).isValid(1L).get()).isEqualTo(1L);
            assertThat(Validator.Max(1F).isValid(1F).get()).isEqualTo(1F);
            assertThat(Validator.Max(1D).isValid(1D).get()).isEqualTo(1D);

            assertThat(Validator.Max(1).isValid(2).swap().get()).containsExactly("must be less than or equal to 1");
            assertThat(Validator.Max(1L).isValid(2L).swap().get()).containsExactly("must be less than or equal to 1");
            assertThat(Validator.Max(1F).isValid(2F).swap().get())
                    .containsExactly("must be less than or equal to 1.000000");
            assertThat(Validator.Max(1D).isValid(2D).swap().get())
                    .containsExactly("must be less than or equal to 1.000000");
        }
    }

    @Nested
    @DisplayName("Validates Strings")
    class Strings {
        @Test
        public void can_validate() {
            assertThat(Validator.MinLength(1).isValid("a").get()).isEqualTo("a");
            assertThat(Validator.MaxLength(1).isValid("a").get()).isEqualTo("a");

            assertThat(Validator.MinLength(2).isValid("a").swap().get())
                    .containsExactly("must be greater than or equal to 2");
            assertThat(Validator.MaxLength(2).isValid("aaa").swap().get())
                    .containsExactly("must be less than or equal to 2");

            assertThat(Validator.Matches("\\d{2}\\w{2}").isValid("00aa").get()).isEqualTo("00aa");
            assertThat(Validator.Matches("\\d{2}\\w{2}")
                            .isValid("foobar")
                            .swap()
                            .get())
                    .containsExactly("must match \\d{2}\\w{2}");

            assertThat(Validator.Matches(Pattern.compile("\\d{2}\\w{2}"))
                            .isValid("00aa")
                            .get())
                    .isEqualTo("00aa");
            assertThat(Validator.Matches(Pattern.compile("\\d{2}\\w{2}"))
                            .isValid("foobar")
                            .swap()
                            .get())
                    .containsExactly("must match \\d{2}\\w{2}");
        }
    }

    @Nested
    @DisplayName("Validates Dates")
    class Dates {
        @Test
        void can_validate() {
            assertThat(Validator.withClock(Clock.fixed(EPOCH, UTC))
                            .<Instant>Future()
                            .isValid(EPOCH.plus(1, ChronoUnit.DAYS))
                            .isRight())
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Combining Validators with AND")
    class And {
        @Test
        void can_combine_validators() {
            var inputs = List.of(
                    new Tuple3<>(alwaysValid, alwaysValid, true),
                    new Tuple3<>(alwaysValid, alwaysInvalid, false),
                    new Tuple3<>(alwaysInvalid, alwaysValid, false),
                    new Tuple3<>(alwaysInvalid, alwaysInvalid, false));

            inputs.forEach(args -> {
                Boolean isValid = args._1.and(args._2).isValid(null).fold(it -> false, it -> true);

                assertThat(isValid).isEqualTo(args._3);
            });
        }

        @Test
        void can_nest() {
            Boolean isValid = alwaysValid
                    .and(alwaysValid.and(alwaysInvalid))
                    .isValid(null)
                    .fold(it -> false, it -> true);
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Combining Validators with OR")
    class Or {
        @Test
        void can_combine_validators() {
            var inputs = List.of(
                    new Tuple3<>(alwaysValid, alwaysValid, true),
                    new Tuple3<>(alwaysValid, alwaysInvalid, true),
                    new Tuple3<>(alwaysInvalid, alwaysValid, true),
                    new Tuple3<>(alwaysInvalid, alwaysInvalid, false));

            inputs.forEach(args -> {
                Boolean isValid = args._1.or(args._2).isValid(null).fold(it -> false, it -> true);

                assertThat(isValid).isEqualTo(args._3);
            });
        }

        @Test
        void can_nest() {
            Boolean isValid = alwaysInvalid
                    .or(alwaysInvalid.or(alwaysValid))
                    .isValid(null)
                    .fold(it -> false, it -> true);
            assertThat(isValid).isTrue();
        }
    }
}
