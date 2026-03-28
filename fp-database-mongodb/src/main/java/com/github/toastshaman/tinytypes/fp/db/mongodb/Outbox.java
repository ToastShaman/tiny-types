package com.github.toastshaman.tinytypes.fp.db.mongodb;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Outbox {

    void store(OutboxEntry entry);

    void storeAll(List<OutboxEntry> entries);

    List<OutboxEntry> findUnprocessed(int limit);

    Optional<OutboxEntry> findById(UUID id);

    void markProcessed(UUID id, Instant processedAt);

    void markProcessed(UUID id);

    long countUnprocessed();

    void deleteProcessed();
}
