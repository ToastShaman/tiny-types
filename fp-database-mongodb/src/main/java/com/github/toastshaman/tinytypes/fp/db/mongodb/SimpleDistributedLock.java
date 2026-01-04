package com.github.toastshaman.tinytypes.fp.db.mongodb;

import io.vavr.Function0;
import io.vavr.Function1;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

public record SimpleDistributedLock(LockProvider provider, LockConfiguration config) implements DistributedLock {

    public SimpleDistributedLock {
        Objects.requireNonNull(provider, "LockProvider must not be null");
        Objects.requireNonNull(config, "LockConfiguration must not be null");
    }

    @Override
    public <R> Optional<R> executeMaybe(Function0<R> fn) {
        return provider.lock(config).map(_ -> fn.apply());
    }

    @Override
    public <R> Optional<R> executeMaybe(Function1<SimpleLock, R> fn) {
        return provider.lock(config).map(fn::apply);
    }

    @Override
    public void runMaybe(Runnable runnable) {
        executeMaybe(() -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public void runMaybe(Consumer<SimpleLock> fn) {
        executeMaybe(lock -> {
            fn.accept(lock);
            return null;
        });
    }
}
