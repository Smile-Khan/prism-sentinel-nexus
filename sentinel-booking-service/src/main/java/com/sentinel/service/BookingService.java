package com.sentinel.service;

import com.sentinel.dto.TicketSoldEvent;
import com.sentinel.model.Seat;
import com.sentinel.producer.BookingEventProducer;
import com.sentinel.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * Core Service orchestrating the High-Concurrency Booking Flow.
 * <p>
 * Implements a Distributed Locking mechanism (Redisson) to prevent Race Conditions
 * during Flash Sales. Transaction boundaries are strictly managed to minimize
 * Connection Pool contention.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final SeatRepository seatRepository;
    private final RedissonClient redissonClient;
    private final BookingEventProducer eventProducer;

    private static final long LOCK_WAIT_TIME = 5;
    private static final long LOCK_LEASE_TIME = 10;

    /**
     * Attempts to reserve a seat under high concurrency.
     *
     * @param seatId The ID of the seat to reserve.
     * @return Operation result status.
     */
    public String reserveSeat(Long seatId) {
        final String lockKey = "lock:seat:" + seatId;
        final RLock lock = redissonClient.getLock(lockKey);

        try {
            // Attempt to acquire distributed lock
            boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);

            if (!isLocked) {
                log.warn("Acquisition timeout for SeatID: {}", seatId);
                return "SYSTEM_BUSY";
            }

            try {
                // Execute Transactional Business Logic
                boolean success = executeReservationTransaction(seatId);

                if (success) {
                    // Emit event ONLY if transaction committed successfully.
                    // Done asynchronously to avoid blocking the return.
                    eventProducer.emit(TicketSoldEvent.create(seatId));
                    return "SUCCESS";
                } else {
                    return "ALREADY_RESERVED";
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during reservation for SeatID: {}", seatId);
            return "INTERRUPTED";
        } catch (Exception e) {
            log.error("Unexpected error reserving SeatID: {}", seatId, e);
            return "ERROR";
        }
    }

    /**
     * Isolated transaction to persist the reservation state.
     * Marked as REQUIRES_NEW to ensure a fresh persistence context if needed,
     * though REQUIRED is usually sufficient.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected boolean executeReservationTransaction(Long seatId) {
        return seatRepository.findById(seatId)
                .map(seat -> {
                    if (seat.isReserved()) {
                        return false;
                    }
                    seat.setReserved(true);
                    seatRepository.save(seat);
                    log.info("Reservation persisted for SeatID: {}", seatId);
                    return true;
                })
                .orElseThrow(() -> new IllegalArgumentException("Seat ID not found: " + seatId));
    }
}