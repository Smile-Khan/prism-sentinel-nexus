package com.smile.prism.command.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile.prism.command.domain.Event;
import com.smile.prism.command.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * The Bridge Consumer.
 * Listens to High-Velocity Sentinel Signals and persists them into the Prism Write-Model.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SentinelIngestor {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Consumes the 'sentinel.public.ticketing' topic.
     * We use String payload + Manual JSON parsing to decouple Prism from Sentinel's internal DTOs.
     */
    @Transactional
    @KafkaListener(topics = "sentinel.public.ticketing", groupId = "prism-ingest-group")
    public void ingestSentinelSignal(@Payload String message,
                                     @Header(KafkaHeaders.RECEIVED_KEY) String seatId) {
        try {
            log.info("⚡ [Prism] Signal Received for Seat: {}", seatId);

            // 1. Parse the Raw JSON
            JsonNode root = objectMapper.readTree(message);
            String status = root.path("status").asText("UNKNOWN");
            String timestamp = root.path("timestamp").asText();

            // 2. Map to Prism's Generic Domain Entity
            // We turn a specific "Ticket Sold" into a generic "Discovery Event"
            Event prismEvent = Event.builder()
                    .title("Seat Reservation: " + seatId)
                    .category("TICKETING")
                    .status(Event.EventStatus.RECEIVED)
                    .metadata(Map.of(
                            "source", "SENTINEL",
                            "external_status", status,
                            "seat_id", seatId,
                            "occurred_at", timestamp
                    ))
                    .build();

            // 3. Persist to Postgres (Triggering Debezium -> Elasticsearch)
            eventRepository.save(prismEvent);

            log.info("💾 [Prism] Signal persisted. Event ID: {}", prismEvent.getId());

        } catch (JsonProcessingException e) {
            log.error("❌ [Prism] Malformed Payload: {}", message, e);
            // In Production: Send to Dead Letter Queue (DLQ)
        }
    }
}