package com.github.toastshaman.tinytypes.fp.db.mongodb;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

public record SimpleDistributedLock(LockProvider provider, LockConfiguration config) implements DistributedLock {

    public SimpleDistributedLock {
        Objects.requireNonNull(provider, "LockProvider must not be null");
        Objects.requireNonNull(config, "LockConfiguration must not be null");
    }

    @Override
    public <R> Optional<R> tryExecuteWithLock(Supplier<R> action) {
        return provider.lock(config).map(_ -> action.get());
    }

    @Override
    public <R> Optional<R> tryExecuteWithLock(Function<SimpleLock, R> action) {
        return provider.lock(config).map(action);
    }

    @Override
    public void tryRunWithLock(Runnable action) {
        tryExecuteWithLock(() -> {
            action.run();
            return null;
        });
    }

    @Override
    public void tryRunWithLock(Consumer<SimpleLock> action) {
        tryExecuteWithLock(lock -> {
            action.accept(lock);
            return null;
        });
    }
}
