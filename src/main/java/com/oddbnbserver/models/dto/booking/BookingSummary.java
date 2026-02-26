package com.oddbnbserver.models.dto.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingSummary {

    private Long bookingId;

    private Long listingId;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guestsCount;

    private Long nights;

    private Double totalPrice;

    private String status;

    private String message;
}