package com.github.toastshaman.tinytypes.fp.db.mongodb;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bson.Document;

@RecordBuilder
public record OutboxEntry(
        UUID id,
        String aggregateType,
        String aggregateId,
        String eventType,
        Document payload,
        Map<String, String> headers,
        Instant createdAt,
        Instant processedAt)
        implements OutboxEntryBuilder.With {

    public OutboxEntry {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(aggregateType, "aggregateType must not be null");
        Objects.requireNonNull(aggregateId, "aggregateId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(headers, "headers must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static OutboxEntryBuilder builder() {
        return OutboxEntryBuilder.builder()
                .id(UUID.randomUUID())
                .headers(Map.of())
                .createdAt(Instant.now())
                .processedAt(null);
    }

    public boolean isProcessed() {
        return processedAt != null;
    }

    public Document toDocument() {
        var doc = new Document()
                .append("_id", id.toString())
                .append("aggregateType", aggregateType)
                .append("aggregateId", aggregateId)
                .append("eventType", eventType)
                .append("payload", payload)
                .append("headers", new Document(headers))
                .append("createdAt", createdAt);

        if (processedAt != null) {
            doc.append("processedAt", processedAt);
        }

        return doc;
    }

    public static OutboxEntry fromDocument(Document doc) {
        return new OutboxEntry(
                UUID.fromString(doc.getString("_id")),
                doc.getString("aggregateType"),
                doc.getString("aggregateId"),
                doc.getString("eventType"),
                doc.get("payload", Document.class),
                toStringMap(doc.get("headers", Document.class)),
                toInstant(doc.getDate("createdAt")),
                toInstant(doc.getDate("processedAt")));
    }

    private static Instant toInstant(java.util.Date date) {
        return date != null ? date.toInstant() : null;
    }

    private static Map<String, String> toStringMap(Document doc) {
        if (doc == null) {
            return Map.of();
        }
        var result = new java.util.HashMap<String, String>();
        for (var entry : doc.entrySet()) {
            result.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return Map.copyOf(result);
    }
}
