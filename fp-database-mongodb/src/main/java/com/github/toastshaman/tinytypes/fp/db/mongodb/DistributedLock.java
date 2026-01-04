package com.github.toastshaman.tinytypes.fp.db.mongodb;

import io.vavr.Function0;
import io.vavr.Function1;
import java.util.Optional;
import net.javacrumbs.shedlock.core.SimpleLock;

public interface DistributedLock {
    <R> Optional<R> executeMaybe(Function0<R> fn);

    <R> Optional<R> executeMaybe(Function1<SimpleLock, R> fn);

    boolean runMaybe(Runnable runnable);

    boolean runMaybe(Function0<SimpleLock> fn);

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
            public boolean runMaybe(Runnable runnable) {
                runnable.run();
                return true;
            }

            @Override
            public boolean runMaybe(Function0<SimpleLock> fn) {
                fn.apply();
                return true;
            }
        };
    }
}
