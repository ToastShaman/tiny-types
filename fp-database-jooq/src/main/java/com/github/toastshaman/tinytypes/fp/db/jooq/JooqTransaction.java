package com.github.toastshaman.tinytypes.fp.db.jooq;

import com.github.toastshaman.tinytypes.fp.Reader;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jooq.Configuration;
import org.jooq.DSLContext;

public final class JooqTransaction<R> {

    private final Function<Configuration, R> action;

    public JooqTransaction(Function<Configuration, R> action) {
        this.action = Objects.requireNonNull(action);
    }

    public static <T> JooqTransaction<T> of(Function<Configuration, T> action) {
        return new JooqTransaction<>(action);
    }

    public static JooqTransaction<Void> run(Consumer<Configuration> action) {
        return new JooqTransaction<>(ctx -> {
            action.accept(ctx);
            return null;
        });
    }

    public <U> JooqTransaction<U> map(Function<R, U> mapper) {
        return new JooqTransaction<>(action.andThen(mapper));
    }

    public <U> JooqTransaction<U> flatMap(Function<R, JooqTransaction<U>> mapper) {
        return new JooqTransaction<>(
                ctx -> action.andThen(mapper).apply(ctx).action.apply(ctx));
    }

    public <U> JooqTransaction<U> flatMap(JooqTransaction<U> other) {
        return flatMap(it -> other);
    }

    public JooqTransaction<Optional<R>> maybe() {
        return JooqTransaction.of(ctx -> Optional.ofNullable(action.apply(ctx)));
    }

    public JooqTransaction<Try<R>> asTry() {
        return JooqTransaction.of(ctx -> Try.of(() -> action.apply(ctx)));
    }

    public <U> JooqTransaction<U> andThen(JooqTransaction<U> other) {
        return JooqTransaction.of(ctx -> {
            action.apply(ctx);
            return other.action.apply(ctx);
        });
    }

    public <U> JooqTransaction<Tuple2<R, U>> zip(JooqTransaction<U> other) {
        return flatMap(a -> other.map(b -> new Tuple2<>(a, b)));
    }

    public Reader<DSLContext, R> result() {
        return Reader.of(ctx -> ctx.transactionResult(action::apply));
    }

    public R apply(DSLContext context) {
        return result().apply(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JooqTransaction<?> that = (JooqTransaction<?>) o;
        return Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action);
    }
}
