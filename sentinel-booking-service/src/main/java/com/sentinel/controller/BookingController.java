package com.sentinel.controller;

import com.sentinel.dto.BookingResponse;
import com.sentinel.model.Seat;
import com.sentinel.repository.SeatRepository;
import com.sentinel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing high-concurrency seat reservation workflows.
 * Exposes endpoints for resource discovery and atomic booking operations.
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SeatRepository seatRepository;

    /**
     * Retrieves the current inventory of unreserved seats.
     */
    @GetMapping("/available")
    public List<Seat> getAvailableSeats() {
        return seatRepository.findByIsReservedFalse();
    }

    /**
     * Handles the atomic reservation request for a specific seat resource.
     * Utilizes a distributed locking mechanism to ensure data consistency under load.
     */
    @PostMapping("/reserve/{seatId}")
    public ResponseEntity<BookingResponse> reserve(@PathVariable Long seatId) {
        String result = bookingService.reserveSeat(seatId);

        return switch (result) {
            case "SUCCESS" -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BookingResponse("Seat reserved successfully", seatId, "SUCCESS"));

            case "ALREADY_RESERVED" -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new BookingResponse("Resource state conflict: Seat already taken", seatId, "TAKEN"));

            default -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new BookingResponse("Request failed: Lock acquisition timeout or system busy", seatId, "RETRY"));
        };
    }
}