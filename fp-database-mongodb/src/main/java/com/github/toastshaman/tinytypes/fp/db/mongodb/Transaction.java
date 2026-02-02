package com.github.toastshaman.tinytypes.fp.db.mongodb;

import com.mongodb.client.MongoClient;
import java.util.Objects;
import java.util.function.Function;

public final class Transaction<R> {

    private final Function<MongoClient, R> fn;

    public Transaction(Function<MongoClient, R> fn) {
        this.fn = Objects.requireNonNull(fn, "function must not be null");
    }

    public <U> Transaction<U> map(Function<R, U> fn) {
        return new Transaction<>(this.fn.andThen(fn));
    }

    public <U> Transaction<U> flatMap(Function<R, Transaction<U>> fn) {
        return new Transaction<>(client -> {
            R result = this.fn.apply(client);
            return fn.apply(result).fn.apply(client);
        });
    }

    public void run(MongoClient client) {
        try (var session = client.startSession()) {
            session.withTransaction(() -> {
                fn.apply(client);
                return null;
            });
        }
    }

    public R execute(MongoClient client) {
        try (var session = client.startSession()) {
            return session.withTransaction(() -> fn.apply(client));
        }
    }

    public static <R> Transaction<R> of(Function<MongoClient, R> fn) {
        return new Transaction<>(fn);
    }
}
