package com.sentinel.dto;

public record BookingResponse(
        String message,
        Long seatId,
        String status
) {}