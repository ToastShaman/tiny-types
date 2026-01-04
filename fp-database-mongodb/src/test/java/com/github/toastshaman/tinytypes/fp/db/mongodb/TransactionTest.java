package com.github.toastshaman.tinytypes.fp.db.mongodb;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@DisplayNameGeneration(ReplaceUnderscores.class)
@Testcontainers
class TransactionTest {

    @Container
    MongoDBContainer container = new MongoDBContainer("mongo").withReplicaSet();

    @Test
    void execute_should_insert_and_return_document_count() {
        var dbName = "testdb";
        var collectionName = "testcol";

        try (var client = MongoClients.create(container.getConnectionString())) {
            var insertAndCount = Transaction.of(ctx -> {
                var db = ctx.getDatabase(dbName);
                db.getCollection(collectionName).insertOne(new Document("name", "test"));
                return db.getCollection(collectionName).countDocuments();
            });

            long count = insertAndCount.execute(client);

            assertEquals(1L, count);
        }
    }

    @Test
    void map_should_transform_result() {
        var dbName = "testdb2";
        var collectionName = "testcol2";

        try (var client = MongoClients.create(container.getConnectionString())) {
            var insert = Transaction.of(ctx -> {
                var db = ctx.getDatabase(dbName);
                db.getCollection(collectionName).insertOne(new Document("name", "foo"));
                return db.getCollection(collectionName).countDocuments();
            });

            var mapped = insert.map("Total: %s"::formatted);

            var result = mapped.execute(client);

            assertEquals("Total: 1", result);
        }
    }

    @Test
    void flatMap_should_chain_transactions() {
        var dbName = "testdb3";
        var collectionName = "testcol3";

        try (var client = MongoClients.create(container.getConnectionString())) {
            var insert = Transaction.of(ctx -> {
                var db = ctx.getDatabase(dbName);
                db.getCollection(collectionName).insertOne(new Document("name", "bar"));
                return db.getCollection(collectionName).countDocuments();
            });

            var flatMapped = insert.flatMap(count -> Transaction.of(_ -> "Inserted: %s".formatted(count)));

            var result = flatMapped.execute(client);

            assertEquals("Inserted: 1", result);
        }
    }
}
