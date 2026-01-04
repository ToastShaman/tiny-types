package com.github.toastshaman.tinytypes.fp.db.mongodb;

import io.vavr.Function0;
import io.vavr.Function1;
import java.util.Objects;
import java.util.Optional;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

public record Lock(LockProvider provider, LockConfiguration config) {

    public Lock {
        Objects.requireNonNull(provider, "LockProvider must not be null");
        Objects.requireNonNull(config, "LockConfiguration must not be null");
    }

    public <R> Optional<R> executeMaybe(Function0<R> fn) {
        return executeMaybe(_ -> fn.apply());
    }

    public <R> Optional<R> executeMaybe(Function1<SimpleLock, R> fn) {
        SimpleLock lock = provider.lock(config).orElse(null);

        if (lock == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(fn.apply(lock));
        } finally {
            lock.unlock();
        }
    }

    public boolean runMaybe(Runnable runnable) {
        return executeMaybe(_ -> {
                    runnable.run();
                    return true;
                })
                .orElse(false);
    }

    public boolean runMaybe(Function0<SimpleLock> fn) {
        return executeMaybe(_ -> {
                    fn.apply();
                    return true;
                })
                .orElse(false);
    }
}
