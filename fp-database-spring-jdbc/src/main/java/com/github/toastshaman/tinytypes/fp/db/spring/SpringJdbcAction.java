package com.github.toastshaman.tinytypes.fp.db.spring;

import io.vavr.control.Either;
import io.vavr.control.Try;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public final class SpringJdbcAction<R> {
    private final Function<NamedParameterJdbcTemplate, R> f;

    public SpringJdbcAction(Function<NamedParameterJdbcTemplate, R> fn) {
        this.f = Objects.requireNonNull(fn);
    }

    public static <R> SpringJdbcAction<R> of(Function<NamedParameterJdbcTemplate, R> fn) {
        return new SpringJdbcAction<>(fn);
    }

    public static SpringJdbcAction<Void> run(Consumer<NamedParameterJdbcTemplate> fn) {
        return new SpringJdbcAction<>(t -> {
            fn.accept(t);
            return null;
        });
    }

    public <V> SpringJdbcAction<V> map(Function<R, V> fn) {
        return new SpringJdbcAction<>(f.andThen(fn));
    }

    public <V> SpringJdbcAction<V> flatMap(Function<R, SpringJdbcAction<V>> fn) {
        return new SpringJdbcAction<>(t -> fn.apply(f.apply(t)).execute(t));
    }

    public R execute(NamedParameterJdbcTemplate jdbcTemplate) {
        return f.apply(jdbcTemplate);
    }

    public SpringJdbcAction<Try<R>> asTry() {
        return new SpringJdbcAction<>(t -> Try.of(() -> f.apply(t)));
    }

    public SpringJdbcAction<Either<Throwable, R>> asEither() {
        return asTry().map(Try::toEither);
    }

    public <V> SpringJdbcAction<V> andThen(SpringJdbcAction<V> after) {
        return new SpringJdbcAction<>(t -> {
            f.apply(t);
            return after.execute(t);
        });
    }

    public SpringJdbcAction<R> withTransaction(TransactionTemplate txTemplate) {
        return new SpringJdbcAction<>(t -> txTemplate.execute(status -> f.apply(t)));
    }

    public static <R1, R2, U> SpringJdbcAction<U> zip(
            SpringJdbcAction<R1> r1, SpringJdbcAction<R2> r2, BiFunction<R1, R2, U> f) {
        return r1.flatMap(v1 -> r2.map(v2 -> f.apply(v1, v2)));
    }

    public static <R1, R2, U> SpringJdbcAction<U> flatZip(
            SpringJdbcAction<R1> r1, SpringJdbcAction<R2> r2, BiFunction<R1, R2, SpringJdbcAction<U>> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> f.apply(v1, v2)));
    }

    public static <R> SpringJdbcAction<List<R>> flatten(List<SpringJdbcAction<R>> l) {
        return new SpringJdbcAction<>(t -> l.stream().map(it -> it.execute(t)).toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpringJdbcAction<?> that = (SpringJdbcAction<?>) o;
        return Objects.equals(f, that.f);
    }

    @Override
    public int hashCode() {
        return Objects.hash(f);
    }
}
