package com.sentinel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "seats")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    private String eventName;
    private BigDecimal price;

    private boolean isReserved = false;

    // This is the Fail-Safe! It prevents race conditions at the DB level.
    @Version
    private Long version;
}