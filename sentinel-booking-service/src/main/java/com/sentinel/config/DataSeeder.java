package com.sentinel.config;

import com.sentinel.model.Seat;
import com.sentinel.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        if (seatRepository.count() == 0) {
            log.info("Initializing demo data: Generating 1000 available seats...");

            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                Seat seat = new Seat();
                seat.setSeatNumber("S-" + i);
                seat.setEventName("Grand Central Concert 2025");
                seat.setPrice(new BigDecimal("150.00"));
                seat.setReserved(false);
                seats.add(seat);
            }

            seatRepository.saveAll(seats);
            log.info("Successfully seeded 1000 seats into the database.");
        }
    }
}