package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.control.Try;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TriesTest {

    @Test
    void zip_two_successes_combines() {
        var result = Tries.zip(Try.success(2), Try.success(3), Integer::sum);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isEqualTo(5);
    }

    @Test
    void zip_two_with_failure_propagates_failure() {
        var ex = new RuntimeException("fail");
        var result = Tries.zip(Try.success(1), Try.failure(ex), Integer::sum);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isEqualTo(ex);
    }

    @Test
    void zip_three_successes_combines() {
        var result = Tries.zip(Try.success(1), Try.success(2), Try.success(3), (a, b, c) -> a + b + c);
        assertThat(result.get()).isEqualTo(6);
    }

    @Test
    void zip_four_successes_combines() {
        var result = Tries.zip(
                Try.success(1), Try.success(2), Try.success(3), Try.success(4), (a, b, c, d) -> a + b + c + d);
        assertThat(result.get()).isEqualTo(10);
    }

    @Test
    void flatZip_two_success() {
        var result = Tries.flatZip(Try.success(2), Try.success(3), (a, b) -> Try.success(a * b));
        assertThat(result.get()).isEqualTo(6);
    }

    @Test
    void flatZip_three_success() {
        var result = Tries.flatZip(
                Try.success("a"), Try.success("b"), Try.success("c"), (x, y, z) -> Try.success(x + y + z));
        assertThat(result.get()).isEqualTo("abc");
    }

    @Test
    void partition_separates_successes_and_failures() {
        var one = new RuntimeException("one");
        var two = new RuntimeException("two");
        var list = List.<Try<Integer>>of(Try.success(1), Try.failure(one), Try.success(2), Try.failure(two));
        var part = Tries.partition(list);
        assertThat(part._1).containsExactly(1, 2);
        assertThat(part._2).containsExactly(one, two);
    }

    @Test
    void anyValues_returns_only_successes() {
        var list = List.<Try<String>>of(Try.success("x"), Try.failure(new RuntimeException()), Try.success("y"));
        var values = Tries.anyValues(list);
        assertThat(values).containsExactly("x", "y");
    }

    @Test
    void allValues_success_when_no_failures() {
        var list = List.of(Try.success(1), Try.success(2));
        var result = Tries.allValues(list);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).containsExactly(1, 2);
    }

    @Test
    void allValues_failure_if_any_error() {
        var ex = new RuntimeException("err");
        var list = List.<Try<Integer>>of(Try.success(1), Try.failure(ex));
        var result = Tries.allValues(list);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isEqualTo(ex);
    }

    @Test
    void foldResult_accumulates_successfully() {
        var values = List.of(1, 2, 3, 4);
        var result = Tries.foldResult(values, Try.success(0), (acc, i) -> Try.success(acc + i));
        assertThat(result.get()).isEqualTo(10);
    }

    @Test
    void foldResult_respects_initial_failure() {
        var ex = new RuntimeException("init");
        Try<Integer> result = Tries.foldResult(List.of(1, 2), Try.failure(ex), (acc, i) -> Try.success(acc + i));
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isEqualTo(ex);
    }

    @Test
    void mapAllValues_collects_or_fails_first_error() {
        var ok = Tries.mapAllValues(List.of("1", "2"), s -> Try.of(() -> Integer.valueOf(s)));
        assertThat(ok.isSuccess()).isTrue();
        assertThat(ok.get()).containsExactly(1, 2);

        var err = Tries.mapAllValues(List.of("1", "x", "3"), s -> Try.of(() -> Integer.valueOf(s)));
        assertThat(err.isFailure()).isTrue();
        assertThat(err.getCause()).isInstanceOf(NumberFormatException.class);
    }

    @Test
    void toResult_converts_try_to_result() {
        var a = Tries.toResult(Try.success(5));
        assertThat(a.isSuccess()).isTrue();
        assertThat(a.getOrNull()).isEqualTo(5);

        var ex = new RuntimeException("z");
        Result<Integer, Throwable> b = Tries.toResult(Try.failure(ex));
        assertThat(b.isFailure()).isTrue();
        assertThat(((Result.Failure<Integer, Throwable>) b).reason()).isEqualTo(ex);
    }
}
