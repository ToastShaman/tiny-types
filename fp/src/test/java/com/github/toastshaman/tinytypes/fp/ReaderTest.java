package com.github.toastshaman.tinytypes.fp;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ReaderTest {

    @Test
    void apply_returns_value() {
        Reader<Integer, Integer> r = Reader.of(x -> x + 1);
        assertThat(r.apply(5)).isEqualTo(6);
    }

    @Test
    void maybe_wraps_non_null_and_null() {
        Reader<String, Integer> lengthReader = Reader.of(String::length);
        assertThat(lengthReader.maybe("abc")).contains(3);

        Reader<String, String> nullReader = Reader.of(s -> null);
        assertThat(nullReader.maybe("any")).isEmpty();
    }

    @Test
    void asOption_converts_to_option() {
        Reader<String, String> r = Reader.of(String::toUpperCase);
        Option<String> opt = r.asOption("hi");
        assertThat(opt.isDefined()).isTrue();
        assertThat(opt.get()).isEqualTo("HI");
    }

    @Test
    void asResult_catches_exception() {
        Reader<String, Integer> bad = Reader.of(s -> {
            throw new IllegalStateException("err");
        });
        Result<Integer, Throwable> res = bad.asResult("x");
        assertThat(res.isFailure()).isTrue();
        assertThat(((Result.Failure<Integer, Throwable>) res).reason())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("err");
    }

    @Test
    void asTry_and_asEither_handle_null() {
        Reader<String, String> nullReader = Reader.of(s -> null);
        Try<String> t = nullReader.asTry("x");
        assertThat(t.isFailure()).isTrue();

        Either<Throwable, String> e = nullReader.asEither("x");
        assertThat(e.isLeft()).isTrue();
    }

    @Test
    void stream_produces_single_element() {
        Reader<Integer, Integer> r = Reader.of(x -> x * 2);
        assertThat(r.stream(3).collect(Collectors.toList())).containsExactly(6);
    }

    @Test
    void pure_ignores_input() {
        Reader<String, Integer> r = Reader.pure(42);
        assertThat(r.apply("anything")).isEqualTo(42);
    }

    @Test
    void fold_two_three_four_readers() {
        Reader<Integer, Integer> r1 = Reader.of(x -> x + 1);
        Reader<Integer, Integer> r2 = Reader.of(x -> x + 2);
        Reader<Integer, Integer> r3 = Reader.of(x -> x + 3);
        Reader<Integer, Integer> r4 = Reader.of(x -> x + 4);

        Tuple2<Integer, Integer> t2 = Reader.fold(r1, r2).apply(1);
        assertThat(t2._1).isEqualTo(2);
        assertThat(t2._2).isEqualTo(3);

        Tuple3<Integer, Integer, Integer> t3 = Reader.fold(r1, r2, r3).apply(1);
        assertThat(t3._1).isEqualTo(2);
        assertThat(t3._2).isEqualTo(3);
        assertThat(t3._3).isEqualTo(4);

        Tuple4<Integer, Integer, Integer, Integer> t4 =
                Reader.fold(r1, r2, r3, r4).apply(1);
        assertThat(t4._1).isEqualTo(2);
        assertThat(t4._2).isEqualTo(3);
        assertThat(t4._3).isEqualTo(4);
        assertThat(t4._4).isEqualTo(5);
    }

    @Test
    void zip_two_three_four_readers() {
        Reader<Integer, Integer> r1 = Reader.of(x -> x);
        Reader<Integer, Integer> r2 = Reader.of(x -> x * 2);
        Reader<Integer, Integer> r3 = Reader.of(x -> x * 3);
        Reader<Integer, Integer> r4 = Reader.of(x -> x * 4);

        Reader<Integer, Integer> z2 = Reader.zip(r1, r2, (a, b) -> a + b);
        assertThat(z2.apply(2)).isEqualTo(6);

        Reader<Integer, Integer> z3 = Reader.zip(r1, r2, r3, (a, b, c) -> a + b + c);
        assertThat(z3.apply(2)).isEqualTo(12);

        Reader<Integer, Integer> z4 = Reader.zip(r1, r2, r3, r4, (a, b, c, d) -> a + b + c + d);
        assertThat(z4.apply(2)).isEqualTo(20);
    }

    @Test
    void flatZip_two_three_four_readers() {
        Reader<Integer, Integer> r1 = Reader.of(x -> x);
        Reader<Integer, Integer> r2 = Reader.of(x -> x * 2);
        Reader<Integer, Integer> r3 = Reader.of(x -> x * 3);
        Reader<Integer, Integer> r4 = Reader.of(x -> x * 4);

        Reader<Integer, Integer> f2 = Reader.flatZip(r1, r2, (a, b) -> Reader.pure(a + b));
        assertThat(f2.apply(3)).isEqualTo(3 + 6);

        Reader<Integer, Integer> f3 = Reader.flatZip(r1, r2, r3, (a, b, c) -> Reader.pure(a + b + c));
        assertThat(f3.apply(3)).isEqualTo(3 + 6 + 9);

        Reader<Integer, Integer> f4 = Reader.flatZip(r1, r2, r3, r4, (a, b, c, d) -> Reader.pure(a + b + c + d));
        assertThat(f4.apply(3)).isEqualTo(3 + 6 + 9 + 12);
    }

    @Test
    void equals_and_hashCode_consider_function_identity() {
        Function<String, String> f = String::trim;
        Reader<String, String> r1 = Reader.of(f);
        Reader<String, String> r2 = Reader.of(f);
        Reader<String, String> r3 = Reader.of(String::trim);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1).isNotEqualTo(r3);
    }
}
