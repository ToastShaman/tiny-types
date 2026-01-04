package com.github.toastshaman.tinytypes.fp.db.mongodb;

import io.vavr.Function0;
import io.vavr.Function1;
import java.util.Optional;
import java.util.function.Consumer;
import net.javacrumbs.shedlock.core.SimpleLock;

public interface DistributedLock {

    <R> Optional<R> executeMaybe(Function0<R> fn);

    <R> Optional<R> executeMaybe(Function1<SimpleLock, R> fn);

    void runMaybe(Runnable runnable);

    void runMaybe(Consumer<SimpleLock> fn);

    static DistributedLock noop() {
        return new DistributedLock() {
            @Override
            public <R> Optional<R> executeMaybe(Function0<R> fn) {
                return Optional.ofNullable(fn.apply());
            }

            @Override
            public <R> Optional<R> executeMaybe(Function1<SimpleLock, R> fn) {
                return Optional.ofNullable(fn.apply(null));
            }

            @Override
            public void runMaybe(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void runMaybe(Consumer<SimpleLock> fn) {
                fn.accept(null);
            }
        };
    }
}
