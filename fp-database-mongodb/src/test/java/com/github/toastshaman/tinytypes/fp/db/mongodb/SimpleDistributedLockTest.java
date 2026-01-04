package com.github.toastshaman.tinytypes.fp.db.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Testcontainers
class SimpleDistributedLockTest {

    @Container
    MongoDBContainer container = new MongoDBContainer("mongo").withReplicaSet();

    @Test
    void can_execute_tryRunWithLock() {
        try (var client = MongoClients.create(container.getConnectionString())) {
            var lock = createSimpleLock(client);

            var result = lock.tryRunWithLock(() -> 1L);

            assertThat(result).isPresent().contains(1L);
        }
    }

    @Test
    void can_run_tryRunWithLock() {
        try (var client = MongoClients.create(container.getConnectionString())) {
            var lock = createSimpleLock(client);

            var hasRun = new AtomicBoolean(false);

            lock.tryRunWithLock(() -> hasRun.set(true));

            assertThat(hasRun).isTrue();
        }
    }

    private static @NonNull SimpleDistributedLock createSimpleLock(MongoClient client) {
        var database = client.getDatabase("my-database");
        var lockProvider = new MongoLockProvider(database);
        var lockConfig =
                new LockConfiguration(Instant.now(), "my-lock", Duration.ofSeconds(20), Duration.ofSeconds(10));
        return new SimpleDistributedLock(lockProvider, lockConfig);
    }
}
