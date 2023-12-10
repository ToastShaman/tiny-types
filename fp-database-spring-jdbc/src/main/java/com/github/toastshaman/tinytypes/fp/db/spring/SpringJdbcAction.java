package com.github.toastshaman.tinytypes.fp.db.spring;

import io.vavr.control.Try;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public final class SpringJdbcAction<R> {
    private final Function<NamedParameterJdbcTemplate, R> f;

    public SpringJdbcAction(Function<NamedParameterJdbcTemplate, R> f) {
        this.f = Objects.requireNonNull(f);
    }

    public static <R> SpringJdbcAction<R> of(Function<NamedParameterJdbcTemplate, R> f) {
        return new SpringJdbcAction<>(f);
    }

    public static SpringJdbcAction<Void> run(Consumer<NamedParameterJdbcTemplate> f) {
        return new SpringJdbcAction<>(t -> {
            f.accept(t);
            return null;
        });
    }

    public R apply(NamedParameterJdbcTemplate jdbcTemplate) {
        return f.apply(jdbcTemplate);
    }

    public Supplier<R> lift(NamedParameterJdbcTemplate jdbcTemplate) {
        return () -> f.apply(jdbcTemplate);
    }

    public SpringJdbcAction<Optional<R>> maybe() {
        return new SpringJdbcAction<>(t -> Optional.ofNullable(f.apply(t)));
    }

    public SpringJdbcAction<Try<R>> asTry() {
        return new SpringJdbcAction<>(t -> Try.of(() -> f.apply(t)));
    }

    public <V> SpringJdbcAction<V> andThen(SpringJdbcAction<V> o) {
        return new SpringJdbcAction<>(t -> {
            f.apply(t);
            return o.apply(t);
        });
    }

    public <V> SpringJdbcAction<V> map(Function<R, V> g) {
        return new SpringJdbcAction<>(f.andThen(g));
    }

    public <V> SpringJdbcAction<V> flatMap(Function<R, SpringJdbcAction<V>> g) {
        return new SpringJdbcAction<>(t -> g.apply(f.apply(t)).apply(t));
    }

    public <V> SpringJdbcAction<V> flatMap(SpringJdbcAction<V> g) {
        return flatMap(it -> g);
    }

    public SpringJdbcAction<R> withTransaction(TransactionTemplate txTemplate) {
        return new SpringJdbcAction<>(t -> txTemplate.execute(status -> f.apply(t)));
    }

    public R execute(NamedParameterJdbcTemplate jdbcTemplate) {
        return apply(jdbcTemplate);
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
        return new SpringJdbcAction<>(t -> l.stream().map(it -> it.apply(t)).toList());
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
