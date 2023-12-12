package com.github.toastshaman.tinytypes.fp.db.jooq;

import io.vavr.control.Either;
import io.vavr.control.Try;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jooq.Configuration;
import org.jooq.DSLContext;

public final class Transaction<R> {

    private final Function<Configuration, R> f;

    public Transaction(Function<Configuration, R> f) {
        this.f = Objects.requireNonNull(f);
    }

    public static <T> Transaction<T> of(Function<Configuration, T> fn) {
        return new Transaction<>(fn);
    }

    public static Transaction<Void> run(Consumer<Configuration> fn) {
        return new Transaction<>(ctx -> {
            fn.accept(ctx);
            return null;
        });
    }

    public <U> Transaction<U> map(Function<R, U> fn) {
        return new Transaction<>(f.andThen(fn));
    }

    public <U> Transaction<U> flatMap(Function<R, Transaction<U>> fn) {
        return new Transaction<>(ctx -> f.andThen(fn).apply(ctx).f.apply(ctx));
    }

    public Transaction<Try<R>> asTry() {
        return Transaction.of(ctx -> Try.of(() -> f.apply(ctx)));
    }

    public Transaction<Either<Throwable, R>> asEither() {
        return asTry().map(Try::toEither);
    }

    public <U> Transaction<U> andThen(Transaction<U> after) {
        return Transaction.of(ctx -> {
            f.apply(ctx);
            return after.f.apply(ctx);
        });
    }

    public R execute(DSLContext context) {
        return wrapInTransaction().f.apply(context.configuration());
    }

    private Transaction<R> wrapInTransaction() {
        return Transaction.of(ctx -> ctx.dsl().transactionResult(f::apply));
    }

    public static <R1, R2, U> Transaction<U> zip(Transaction<R1> r1, Transaction<R2> r2, BiFunction<R1, R2, U> f) {
        return r1.flatMap(v1 -> r2.map(v2 -> f.apply(v1, v2)));
    }

    public static <R1, R2, U> Transaction<U> flatZip(
            Transaction<R1> r1, Transaction<R2> r2, BiFunction<R1, R2, Transaction<U>> f) {
        return r1.flatMap(v1 -> r2.flatMap(v2 -> f.apply(v1, v2)));
    }

    public static <R> Transaction<List<R>> flatten(List<Transaction<R>> l) {
        return new Transaction<>(t -> l.stream().map(it -> it.f.apply(t)).toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction<?> that = (Transaction<?>) o;
        return Objects.equals(f, that.f);
    }

    @Override
    public int hashCode() {
        return Objects.hash(f);
    }
}
