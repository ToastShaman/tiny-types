package com.github.toastshaman.tinytypes.fp.db.mongodb;

import io.vavr.Function0;
import io.vavr.Function1;
import java.util.Optional;
import java.util.function.Consumer;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

public record Lock(LockProvider p, LockConfiguration config) {

    public <R> Optional<R> executeMaybe(Function0<R> fn) {
        return executeMaybe(_ -> fn.apply());
    }

    public <R> Optional<R> executeMaybe(Function1<SimpleLock, R> fn) {
        SimpleLock lock = p.lock(config).orElse(null);

        if (lock == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(fn.apply(lock));
        } finally {
            lock.unlock();
        }
    }

    public void runMaybe(Runnable runnable) {
        runMaybe(_ -> runnable.run());
    }

    public void runMaybe(Consumer<SimpleLock> fn) {
        SimpleLock lock = p.lock(config).orElse(null);

        if (lock == null) {
            return;
        }

        try {
            fn.accept(lock);
        } finally {
            lock.unlock();
        }
    }
}
