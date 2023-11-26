package com.github.toastshaman.tinytypes.test.assertions;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.toastshaman.tinytypes.fp.Result;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ThrowingConsumer;

public final class ResultAssertions<T, E> extends AbstractAssert<ResultAssertions<T, E>, Result<T, E>> {

    private ResultAssertions(Result<T, E> actual) {
        super(actual, ResultAssertions.class);
    }

    public static <T, E> ResultAssertions<T, E> assertThatResult(Result<T, E> actual) {
        return new ResultAssertions<>(actual);
    }

    public ResultAssertions<T, E> isSuccess() {
        isNotNull();
        if (actual.isFailure()) {
            failWithMessage("Expected <%s> to be success", actual);
        }
        return this;
    }

    public ResultAssertions<T, E> isFailure() {
        isNotNull();
        if (actual.isSuccess()) {
            failWithMessage("Expected <%s> to be failure", actual);
        }
        return this;
    }

    public ResultAssertions<T, E> hasValue(T expected) {
        isSuccess();
        assertThat(actual.getOrNull()).isEqualTo(expected);
        return this;
    }

    public ResultAssertions<T, E> hasFailure(E expected) {
        isFailure();
        assertThat(((Result.Failure<T, E>) actual).reason()).isEqualTo(expected);
        return this;
    }

    public ResultAssertions<T, E> hasValueSatisfying(ThrowingConsumer<T> requirements) {
        isSuccess();
        assertThat(actual.getOrNull()).satisfies(requirements);
        return this;
    }

    public ResultAssertions<T, E> hasFailureSatisfying(ThrowingConsumer<E> requirements) {
        isFailure();
        assertThat(((Result.Failure<T, E>) actual).reason()).satisfies(requirements);
        return this;
    }
}
