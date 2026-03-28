package com.github.toastshaman.tinytypes.fp.db.mongodb;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.MongoCollection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.bson.Document;

public final class MongoOutbox implements Outbox {

    private final MongoCollection<Document> collection;

    private final Supplier<Instant> clock;

    public MongoOutbox(MongoCollection<Document> collection, Supplier<Instant> clock) {
        this.collection = Objects.requireNonNull(collection, "collection must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public void store(OutboxEntry entry) {
        collection.insertOne(entry.toDocument());
    }

    @Override
    public void storeAll(List<OutboxEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        collection.insertMany(entries.stream().map(OutboxEntry::toDocument).toList());
    }

    @Override
    public List<OutboxEntry> findUnprocessed(int limit) {
        return collection
                .find(exists("processedAt", false))
                .sort(ascending("createdAt"))
                .limit(limit)
                .map(OutboxEntry::fromDocument)
                .into(new ArrayList<>());
    }

    @Override
    public Optional<OutboxEntry> findById(UUID id) {
        var doc = collection.find(eq("_id", id.toString())).first();
        return Optional.ofNullable(doc).map(OutboxEntry::fromDocument);
    }

    @Override
    public void markProcessed(UUID id, Instant processedAt) {
        collection.updateOne(eq("_id", id.toString()), set("processedAt", processedAt));
    }

    @Override
    public void markProcessed(UUID id) {
        markProcessed(id, clock.get());
    }

    @Override
    public long countUnprocessed() {
        return collection.countDocuments(exists("processedAt", false));
    }

    @Override
    public void deleteProcessed() {
        collection.deleteMany(exists("processedAt", true));
    }
}
