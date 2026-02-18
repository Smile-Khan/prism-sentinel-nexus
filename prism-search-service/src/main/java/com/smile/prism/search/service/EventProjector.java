package com.smile.prism.search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile.prism.search.document.EventDocument;
import com.smile.prism.search.repository.EventSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProjector {

    private final EventSearchRepository searchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "prism-cdc.public.events", groupId = "prism-search-group")
    public void processCdcEvent(String message) {
        try {
            JsonNode payload = objectMapper.readTree(message).path("payload");
            String op = payload.path("op").asText();

            if (!"d".equals(op)) {
                JsonNode after = payload.path("after");

                EventDocument doc = EventDocument.builder()
                        .id(UUID.fromString(after.path("id").asText())) // Safe parsing to UUID
                        .title(after.path("title").asText())
                        .category(after.path("category").asText())
                        .status(after.path("status").asText())
                        .build();

                searchRepository.save(doc);
                log.info("[PRISM-PROJECTOR] Document indexed successfully. ID: {}", doc.getId());
            }
        } catch (Exception e) {
            log.error("[PRISM-PROJECTOR] Critical failure during CDC projection: {}", e.getMessage());
        }
    }
}