package com.github.toastshaman.tinytypes.fp.db.jooq;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jooq.Configuration;
import org.jooq.DSLContext;

public final class JooqAction<R> {

    private final Function<Configuration, R> action;

    public JooqAction(Function<Configuration, R> action) {
        this.action = Objects.requireNonNull(action);
    }

    public static <T> JooqAction<T> of(Function<Configuration, T> action) {
        return new JooqAction<>(action);
    }

    public static JooqAction<Void> run(Consumer<Configuration> action) {
        return new JooqAction<>(ctx -> {
            action.accept(ctx);
            return null;
        });
    }

    public <U> JooqAction<U> map(Function<R, U> mapper) {
        return new JooqAction<>(action.andThen(mapper));
    }

    public <U> JooqAction<U> flatMap(Function<R, JooqAction<U>> mapper) {
        return new JooqAction<>(ctx -> action.andThen(mapper).apply(ctx).action.apply(ctx));
    }

    public <U> JooqAction<U> flatMap(JooqAction<U> other) {
        return flatMap(it -> other);
    }

    public JooqAction<Optional<R>> maybe() {
        return JooqAction.of(ctx -> Optional.ofNullable(action.apply(ctx)));
    }

    public JooqAction<Try<R>> asTry() {
        return JooqAction.of(ctx -> Try.of(() -> action.apply(ctx)));
    }

    public JooqAction<Either<Throwable, R>> asEither() {
        return asTry().map(Try::toEither);
    }

    public <U> JooqAction<U> andThen(JooqAction<U> other) {
        return JooqAction.of(ctx -> {
            action.apply(ctx);
            return other.action.apply(ctx);
        });
    }

    public <U> JooqAction<Tuple2<R, U>> zip(JooqAction<U> other) {
        return flatMap(a -> other.map(b -> new Tuple2<>(a, b)));
    }

    public JooqAction<R> withTransaction() {
        return JooqAction.of(ctx -> ctx.dsl().transactionResult(action::apply));
    }

    public R apply(DSLContext context) {
        return action.apply(context.configuration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JooqAction<?> that = (JooqAction<?>) o;
        return Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action);
    }
}
