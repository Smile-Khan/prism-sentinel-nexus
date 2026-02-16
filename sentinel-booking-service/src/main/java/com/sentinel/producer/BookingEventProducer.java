package com.sentinel.producer;

import com.sentinel.dto.TicketSoldEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Handles the asynchronous emission of booking events to the Kafka Event Backbone.
 * <p>
 * This component decouples the persistence layer from the messaging layer,
 * ensuring that network latency in the broker does not block the HTTP response.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic logic should ideally be externalized, but constant is acceptable for single-purpose producers
    private static final String TOPIC_TICKETING = "sentinel.public.ticketing";

    /**
     * Publishes a {@link TicketSoldEvent} to the dedicated Kafka topic.
     * Uses a non-blocking callback mechanism.
     *
     * @param event The immutable event payload.
     */
    public void emit(TicketSoldEvent event) {
        String partitionKey = String.valueOf(event.seatId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC_TICKETING, partitionKey, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // Critical Failure: Event was not acknowledged by the broker.
                // In a production environment, this should trigger an alert or write to a fallback Outbox table.
                log.error("Failed to publish TicketSoldEvent for SeatID: {}. Error: {}",
                        event.seatId(), ex.getMessage(), ex);
            } else {
                log.debug("Event published successfully. SeatID: {}, Offset: {}",
                        event.seatId(), result.getRecordMetadata().offset());
            }
        });
    }
}