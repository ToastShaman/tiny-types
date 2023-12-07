package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.toastshaman.tinytypes.fp.Result.Failure;
import com.github.toastshaman.tinytypes.fp.Result.Success;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ResultTest {

    @Test
    void can_create_success() {
        assertThat(Result.success(1)).isInstanceOf(Success.class);
    }

    @Test
    void can_create_failure() {
        assertThat(Result.failure(1)).isInstanceOf(Failure.class);
    }

    @Test
    void can_create_result_from_supplier() {
        var ok1 = Result.of(() -> 1);
        var ok2 = Result.ofSupplier(() -> 1);
        var ok3 = Result.ofCallable(() -> 1);
        var err = Result.ofSupplier(() -> {
            throw new RuntimeException("oops");
        });

        assertThat(ok1).isInstanceOf(Success.class);
        assertThat(ok2).isInstanceOf(Success.class);
        assertThat(ok3).isInstanceOf(Success.class);
        assertThat(err).isInstanceOf(Failure.class);
    }

    @Test
    void can_partition() {
        var exception = new RuntimeException("oops");
        Result<Integer, Throwable> first = Result.success(1);
        Result<Integer, Throwable> second = Result.failure(exception);
        var results = List.of(first, second);

        var partitioned = Result.partition(results);

        assertThat(partitioned._1).hasSize(1).containsExactly(1);
        assertThat(partitioned._2).hasSize(1).containsExactly(exception);
    }

    @Test
    void any_values_only_returns_successes() {
        Result<Integer, Throwable> first = Result.success(1);
        Result<Integer, Throwable> second = Result.failure(new RuntimeException("oops"));
        var results = List.of(first, second);

        var values = Result.anyValues(results);

        assertThat(values).containsExactly(1);
    }

    @Test
    void all_values_returns_failure_if_one_result_errored() {
        Result<Integer, Throwable> first = Result.success(1);
        Result<Integer, Throwable> second = Result.failure(new RuntimeException("oops"));
        var results = List.of(first, second);

        var values = Result.allValues(results);

        assertThat(values).isInstanceOfSatisfying(Failure.class, it -> assertThat(it.reason())
                .isInstanceOf(RuntimeException.class));
    }

    @Test
    void all_values_returns_success_if_all_result_ok() {
        Result<Integer, Throwable> first = Result.success(1);
        Result<Integer, Throwable> second = Result.success(2);
        var results = List.of(first, second);

        var values = Result.allValues(results);

        assertThat(values).isInstanceOf(Success.class);
        assertThat(values.getOrNull()).containsExactly(1, 2);
    }

    @Test
    void can_zip() {
        Result<Integer, Throwable> first = Result.success(1);
        Result<Double, Throwable> second = Result.success(2.0);

        var zipped = Result.zip(first, second, (a, b) -> a + b);

        assertThat(zipped.getOrNull()).isEqualTo(3.0);
    }

    @Test
    void can_flat_zip() {
        Result<Integer, Throwable> first = Result.success(1);
        Result<Double, Throwable> second = Result.success(2.0);

        var zipped = Result.flatZip(first, second, (a, b) -> new Success<>(a + b));

        assertThat(zipped.getOrNull()).isEqualTo(3.0);
    }

    @Test
    void can_fold_results() {
        Result<Integer, Object> folded =
                Result.foldResult(List.of(1, 2, 3, 4, 5), Result.success(0), (acc, i) -> Result.success(acc + i));

        assertThat(folded).isInstanceOf(Success.class);
        assertThat(folded.getOrNull()).isEqualTo(15);
    }

    @Test
    void can_fold_results_with_failures() {
        Result<Integer, Object> folded = Result.foldResult(List.of(), Result.success(0), (acc, i) -> Result.failure(1));

        assertThat(folded).isInstanceOf(Success.class);
        assertThat(folded.getOrNull()).isEqualTo(0);
    }

    @Test
    void can_fold_results_with_initial_failure() {
        Result<Integer, Object> folded = Result.foldResult(List.of(), Result.failure(0), (acc, i) -> Result.failure(1));

        assertThat(folded).isInstanceOfSatisfying(Failure.class, it -> assertThat(it.reason())
                .isEqualTo(0));
    }

    @Test
    void can_map_all_values_in_a_list() {
        var ok = Result.mapAllValues(List.of(1, 2, 3, 4, 5), it -> Result.success(it + 1));
        var err = Result.mapAllValues(List.of(1, 2, 3, 4, 5), it -> Result.failure(it + 1));

        assertThat(ok).isInstanceOfSatisfying(Success.class, it -> assertThat(it.getOrNull())
                .isEqualTo(List.of(2, 3, 4, 5, 6)));

        assertThat(err).isInstanceOfSatisfying(Failure.class, it -> assertThat(it.reason())
                .isEqualTo(2));
    }

    @Nested
    class SuccessfulResultTest {

        @Test
        void knows_which_type_it_is() {
            var result = getResult();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.isFailure()).isFalse();
        }

        @Test
        void can_map() {
            var result = getResult().map(it -> it + 1);

            assertThat(result).isEqualTo(new Success<>(2));
        }

        @Test
        void can_bimap() {
            var result = getResult().bimap(it -> it + 1, it -> it - 1);

            assertThat(result).isEqualTo(new Success<>(2));
        }

        @Test
        void can_flat_map() {
            var result = getResult().flatMap(it -> new Success<>(it + 1));

            assertThat(result).isEqualTo(new Success<>(2));
        }

        @Test
        void can_map_failure() {
            var result = getResult().mapFailure(it -> it + 1);

            assertThat(result).isEqualTo(new Success<>(1));
        }

        @Test
        void can_flat_map_failure() {
            var result = getResult().flatMapFailure(it -> new Success<>(it + 1));

            assertThat(result).isEqualTo(new Success<>(1));
        }

        @Test
        void can_peek() {
            var idx = new AtomicInteger(0);

            Result.success(5).onSuccess(idx::set);

            assertThat(idx).hasValue(5);
        }

        @Test
        void can_not_peek_on_failure() {
            var idx = new AtomicInteger(0);

            Result.<Integer, Integer>success(5).onFailure(idx::set);

            assertThat(idx).hasValue(0);
        }

        @Test
        void can_get_or_else() {
            var idx = getResult().getOrElse(2);

            assertThat(idx).isEqualTo(1);
        }

        @Test
        void can_get_or_else_get() {
            var idx = getResult().getOrElseGet(() -> 2);

            assertThat(idx).isEqualTo(1);
        }

        @Test
        void can_get_or_null() {
            var idx = getResult().getOrNull();

            assertThat(idx).isNotNull();
        }

        @Test
        void can_get_or_else_throw() {
            assertThatNoException().isThrownBy(() -> getResult().getOrThrow(it -> new RuntimeException(it.toString())));
        }

        @Test
        void can_swap() {
            var result = getResult().swap();

            assertThat(result).isInstanceOfSatisfying(Failure.class, it -> assertThat(it.reason())
                    .isEqualTo(1));
        }

        @Test
        void can_recover() {
            var result = getResult().recover(it -> it + 1);

            assertThat(result).isInstanceOfSatisfying(Success.class, it -> assertThat(it.value())
                    .isEqualTo(1));
        }

        @Test
        void can_return_optional() {
            assertThat(getResult().asOptional()).isNotEmpty();
        }

        @Test
        void can_fold() {
            var idx = getResult().fold(it -> it + 1, it -> it + 2);

            assertThat(idx).isEqualTo(2);
        }

        private static Result<Integer, Integer> getResult() {
            return Result.success(1);
        }
    }

    @Nested
    class FailureResultTest {

        @Test
        void knows_which_type_it_is() {
            var result = getResult();

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.isFailure()).isTrue();
        }

        @Test
        void can_map() {
            var result = getResult().map(it -> it + 1);

            assertThat(result).isEqualTo(new Failure<>(1));
        }

        @Test
        void can_bimap() {
            var result = getResult().bimap(it -> it + 1, it -> it - 1);

            assertThat(result).isEqualTo(new Failure<>(0));
        }

        @Test
        void can_flat_map() {
            var result = getResult().flatMap(it -> new Success<>(it + 1));

            assertThat(result).isEqualTo(new Failure<>(1));
        }

        @Test
        void can_map_failure() {
            var result = getResult().mapFailure(it -> it + 1);

            assertThat(result).isEqualTo(new Failure<>(2));
        }

        @Test
        void can_flat_map_failure() {
            var result = getResult().flatMapFailure(it -> new Failure<>(it + 1));

            assertThat(result).isEqualTo(new Failure<>(2));
        }

        @Test
        void can_peek() {
            var idx = new AtomicInteger(0);

            Result.failure(5).onFailure(idx::set);

            assertThat(idx).hasValue(5);
        }

        @Test
        void can_not_peek_on_success() {
            var idx = new AtomicInteger(0);

            Result.<Integer, Integer>success(5).onFailure(idx::set);

            assertThat(idx).hasValue(0);
        }

        @Test
        void can_get_or_else() {
            var idx = getResult().getOrElse(2);

            assertThat(idx).isEqualTo(2);
        }

        @Test
        void can_get_or_else_get() {
            var idx = getResult().getOrElseGet(() -> 2);

            assertThat(idx).isEqualTo(2);
        }

        @Test
        void can_get_or_null() {
            var idx = getResult().getOrNull();

            assertThat(idx).isNull();
        }

        @Test
        void can_get_or_throws_underlying_exception() {
            assertThatThrownBy(() -> getResult().getOrThrow(it -> new RuntimeException(it.toString())))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void can_swap() {
            var result = getResult().swap();

            assertThat(result).isInstanceOfSatisfying(Success.class, it -> assertThat(it.value())
                    .isEqualTo(1));
        }

        @Test
        void can_recover() {
            var result = getResult().recover(it -> it + 1);

            assertThat(result).isInstanceOfSatisfying(Success.class, it -> assertThat(it.value())
                    .isEqualTo(2));
        }

        @Test
        void can_return_optional() {
            assertThat(getResult().asOptional()).isEmpty();
        }

        @Test
        void can_fold() {
            var idx = getResult().fold(it -> it + 1, it -> it + 2);

            assertThat(idx).isEqualTo(3);
        }

        private static Result<Integer, Integer> getResult() {
            return Result.failure(1);
        }
    }
}
