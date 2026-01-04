package com.github.toastshaman.tinytypes.fp.db.mongodb;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.javacrumbs.shedlock.core.SimpleLock;

public interface DistributedLock {

    <R> Optional<R> tryRunWithLock(Supplier<R> action);

    <R> Optional<R> tryRunWithLock(Function<SimpleLock, R> action);

    void tryRunWithLock(Runnable action);

    void tryRunWithLock(Consumer<SimpleLock> action);

    static DistributedLock noop() {
        return new DistributedLock() {
            @Override
            public <R> Optional<R> tryRunWithLock(Supplier<R> action) {
                return Optional.ofNullable(action.get());
            }

            @Override
            public <R> Optional<R> tryRunWithLock(Function<SimpleLock, R> action) {
                return Optional.ofNullable(action.apply(null));
            }

            @Override
            public void tryRunWithLock(Runnable action) {
                action.run();
            }

            @Override
            public void tryRunWithLock(Consumer<SimpleLock> action) {
                action.accept(null);
            }
        };
    }
}
