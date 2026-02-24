package com.oddbnbserver.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    public enum Status {
        INACTIVE,
        ACTIVE,
        BOOKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guestsCount;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

}