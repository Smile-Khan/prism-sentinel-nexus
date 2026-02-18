package com.smile.prism.search.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Map;
import java.util.UUID;

/**
 * Represents the searchable event projection within the Elasticsearch cluster.
 *
 * This document is the Read-Model component of the CQRS architecture,
 * synchronized via CDC (Debezium) from the primary PostgreSQL store.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setting(shards = 1, replicas = 0)
@Document(indexName = "prism-events")
public class EventDocument {

    /**
     * Unique identifier maintaining parity with the primary database.
     */
    @Id
    private UUID id;

    /**
     * The primary search target. Analyzed using the standard tokenizer
     * to support fuzzy matching and full-text discovery.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    /**
     * Categorization field used for exact-match filtering.
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * Current state of the event within the Prism pipeline.
     */
    @Field(type = FieldType.Keyword)
    private String status;

    /**
     * Flexible metadata captured from the source system.
     * Mapped as an Object to support searching within nested JSON structures.
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> metadata;
}