package com.github.toastshaman.tinytypes.test.assertions;

import static com.github.toastshaman.tinytypes.test.assertions.ResultAssertions.assertThatResult;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.fp.Result;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ResultAssertionsTest {

    @Test
    void can_assert_success() {
        assertThatResult(Result.success(1)).isSuccess().hasValue(1);
    }

    @Test
    void can_assert_failure() {
        assertThatResult(Result.failure(1)).isFailure().hasFailure(1);
    }

    @Test
    void can_assert_success_satisfying() {
        assertThatResult(Result.success(1))
                .hasValueSatisfying(it -> assertThat(it).isEqualTo(1));
    }

    @Test
    void can_assert_failure_satisfying() {
        assertThatResult(Result.failure(1))
                .hasFailureSatisfying(it -> assertThat(it).isEqualTo(1));
    }
}
