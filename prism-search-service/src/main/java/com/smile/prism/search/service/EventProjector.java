package com.smile.prism.search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile.prism.search.document.EventDocument;
import com.smile.prism.search.repository.EventSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProjector {

    private final EventSearchRepository searchRepository;
    private final ObjectMapper objectMapper;

    /**
     * Listens to Debezium CDC events.
     * Debezium topic format: prism-cdc.public.events
     */
    @KafkaListener(topics = "prism-cdc.public.events", groupId = "prism-search-group")
    public void processCdcEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);

            // Debezium structure: { "payload": { "op": "c", "after": { ... data ... } } }
            JsonNode payload = root.path("payload");
            String operation = payload.path("op").asText();

            // We only care about Create (c), Update (u), and Snapshot (r)
            if (!"d".equals(operation)) { // 'd' is delete
                JsonNode after = payload.path("after");

                // Map Postgres columns to Elastic Document
                EventDocument doc = EventDocument.builder()
                        .id(after.get("id").asText())
                        .title(after.get("title").asText())
                        .category(after.get("category").asText())
                        .status(after.get("status").asText())
                        // Metadata is JSONB string in Debezium, needs parsing
                        // For simplicity in this step, we skip complex metadata parsing or map it simply
                        .build();

                searchRepository.save(doc);
                log.info("🔍 [Prism Search] Indexed Document ID: {}", doc.getId());
            }

        } catch (Exception e) {
            log.error("❌ Failed to project event: {}", e.getMessage());
        }
    }
}