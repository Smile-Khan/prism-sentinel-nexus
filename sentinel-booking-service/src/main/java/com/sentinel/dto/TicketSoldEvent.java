package com.sentinel.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable domain event representing a confirmed seat reservation.
 * <p>
 * Implemented as a Java Record to ensure data purity and thread safety during
 * serialization across the distributed event backbone.
 *
 * @param eventId   Unique identifier for idempotency checks (UUID v4).
 * @param seatId    The aggregate root ID (Seat) that was modified.
 * @param status    The current lifecycle state of the reservation.
 * @param timestamp The UTC instant when the reservation occurred.
 */
public record TicketSoldEvent(
        UUID eventId,
        Long seatId,
        String status,
        Instant timestamp
) implements Serializable {

    public static TicketSoldEvent create(Long seatId) {
        return new TicketSoldEvent(
                UUID.randomUUID(),
                seatId,
                "RESERVED",
                Instant.now()
        );
    }
}