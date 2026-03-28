package com.github.toastshaman.tinytypes.fp.db.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Testcontainers
class MongoOutboxTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo").withReplicaSet();

    MongoCollection<Document> collection;
    MongoOutbox outbox;
    Instant fixedTime = Instant.parse("2026-03-28T10:00:00Z");

    @BeforeEach
    void setUp() {
        try (var client = MongoClients.create(container.getConnectionString())) {
            client.getDatabase("test-db").getCollection("outbox").drop();
        }
        var client = MongoClients.create(container.getConnectionString());
        collection = client.getDatabase("test-db").getCollection("outbox");
        outbox = new MongoOutbox(collection, () -> fixedTime);
    }

    @AfterEach
    void tearDown() {
        collection.drop();
    }

    @Nested
    class Store {

        @Test
        void stores_entry_in_collection() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .build();

            outbox.store(entry);

            assertThat(collection.countDocuments()).isEqualTo(1);
        }

        @Test
        void stores_entry_with_all_fields() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .headers(Map.of("traceId", "abc-123"))
                    .build();

            outbox.store(entry);

            var stored = outbox.findById(entry.id()).orElseThrow();
            assertThat(stored.aggregateType()).isEqualTo("Order");
            assertThat(stored.aggregateId()).isEqualTo("order-123");
            assertThat(stored.eventType()).isEqualTo("OrderCreated");
            assertThat(stored.payload().getInteger("amount")).isEqualTo(100);
            assertThat(stored.headers()).containsEntry("traceId", "abc-123");
            assertThat(stored.createdAt()).isNotNull();
            assertThat(stored.processedAt()).isNull();
        }
    }

    @Nested
    class StoreAll {

        @Test
        void stores_multiple_entries() {
            var entries = List.of(
                    OutboxEntry.builder()
                            .aggregateType("Order")
                            .aggregateId("order-1")
                            .eventType("OrderCreated")
                            .payload(new Document("amount", 100))
                            .build(),
                    OutboxEntry.builder()
                            .aggregateType("Order")
                            .aggregateId("order-2")
                            .eventType("OrderCreated")
                            .payload(new Document("amount", 200))
                            .build(),
                    OutboxEntry.builder()
                            .aggregateType("Order")
                            .aggregateId("order-3")
                            .eventType("OrderCreated")
                            .payload(new Document("amount", 300))
                            .build());

            outbox.storeAll(entries);

            assertThat(collection.countDocuments()).isEqualTo(3);
        }

        @Test
        void handles_empty_list() {
            outbox.storeAll(List.of());

            assertThat(collection.countDocuments()).isEqualTo(0);
        }
    }

    @Nested
    class FindUnprocessed {

        @Test
        void returns_unprocessed_entries() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .build();
            outbox.store(entry);

            var unprocessed = outbox.findUnprocessed(10);

            assertThat(unprocessed).hasSize(1);
            assertThat(unprocessed.getFirst().id()).isEqualTo(entry.id());
        }

        @Test
        void excludes_processed_entries() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .build();
            outbox.store(entry);
            outbox.markProcessed(entry.id());

            var unprocessed = outbox.findUnprocessed(10);

            assertThat(unprocessed).isEmpty();
        }

        @Test
        void respects_limit() {
            for (int i = 0; i < 5; i++) {
                outbox.store(OutboxEntry.builder()
                        .aggregateType("Order")
                        .aggregateId("order-" + i)
                        .eventType("OrderCreated")
                        .payload(new Document("amount", i))
                        .build());
            }

            var unprocessed = outbox.findUnprocessed(3);

            assertThat(unprocessed).hasSize(3);
        }

        @Test
        void returns_entries_ordered_by_created_at() {
            var earlier = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-1")
                    .eventType("OrderCreated")
                    .payload(new Document("order", "first"))
                    .createdAt(Instant.parse("2026-03-28T09:00:00Z"))
                    .build();
            var later = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-2")
                    .eventType("OrderCreated")
                    .payload(new Document("order", "second"))
                    .createdAt(Instant.parse("2026-03-28T11:00:00Z"))
                    .build();

            outbox.store(later);
            outbox.store(earlier);

            var unprocessed = outbox.findUnprocessed(10);

            assertThat(unprocessed).hasSize(2);
            assertThat(unprocessed.get(0).aggregateId()).isEqualTo("order-1");
            assertThat(unprocessed.get(1).aggregateId()).isEqualTo("order-2");
        }

        @Test
        void returns_empty_list_when_no_entries() {
            var unprocessed = outbox.findUnprocessed(10);

            assertThat(unprocessed).isEmpty();
        }
    }

    @Nested
    class FindById {

        @Test
        void returns_entry_when_found() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .build();
            outbox.store(entry);

            var found = outbox.findById(entry.id());

            assertThat(found).isPresent();
            assertThat(found.get().id()).isEqualTo(entry.id());
        }

        @Test
        void returns_empty_when_not_found() {
            var found = outbox.findById(UUID.randomUUID());

            assertThat(found).isEmpty();
        }
    }

    @Nested
    class MarkProcessed {

        @Test
        void marks_entry_as_processed_with_timestamp() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .build();
            outbox.store(entry);
            var processedAt = Instant.parse("2026-03-28T12:00:00Z");

            outbox.markProcessed(entry.id(), processedAt);

            var updated = outbox.findById(entry.id()).orElseThrow();
            assertThat(updated.processedAt()).isEqualTo(processedAt);
            assertThat(updated.isProcessed()).isTrue();
        }

        @Test
        void marks_entry_as_processed_using_clock() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-123")
                    .eventType("OrderCreated")
                    .payload(new Document("amount", 100))
                    .build();
            outbox.store(entry);

            outbox.markProcessed(entry.id());

            var updated = outbox.findById(entry.id()).orElseThrow();
            assertThat(updated.processedAt()).isEqualTo(fixedTime);
        }
    }

    @Nested
    class CountUnprocessed {

        @Test
        void returns_count_of_unprocessed_entries() {
            outbox.store(OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-1")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build());
            outbox.store(OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-2")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build());
            outbox.store(OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-3")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build());

            assertThat(outbox.countUnprocessed()).isEqualTo(3);
        }

        @Test
        void excludes_processed_entries_from_count() {
            var entry1 = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-1")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build();
            var entry2 = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-2")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build();
            outbox.store(entry1);
            outbox.store(entry2);
            outbox.markProcessed(entry1.id());

            assertThat(outbox.countUnprocessed()).isEqualTo(1);
        }

        @Test
        void returns_zero_when_empty() {
            assertThat(outbox.countUnprocessed()).isEqualTo(0);
        }
    }

    @Nested
    class DeleteProcessed {

        @Test
        void deletes_processed_entries() {
            var entry1 = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-1")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build();
            var entry2 = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-2")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build();
            outbox.store(entry1);
            outbox.store(entry2);
            outbox.markProcessed(entry1.id());

            outbox.deleteProcessed();

            assertThat(collection.countDocuments()).isEqualTo(1);
            assertThat(outbox.findById(entry1.id())).isEmpty();
            assertThat(outbox.findById(entry2.id())).isPresent();
        }

        @Test
        void keeps_unprocessed_entries() {
            var entry = OutboxEntry.builder()
                    .aggregateType("Order")
                    .aggregateId("order-1")
                    .eventType("OrderCreated")
                    .payload(new Document())
                    .build();
            outbox.store(entry);

            outbox.deleteProcessed();

            assertThat(outbox.findById(entry.id())).isPresent();
        }

        @Test
        void handles_empty_collection() {
            outbox.deleteProcessed();

            assertThat(collection.countDocuments()).isEqualTo(0);
        }
    }
}
