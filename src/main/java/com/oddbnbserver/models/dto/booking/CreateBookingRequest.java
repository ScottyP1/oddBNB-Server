package com.oddbnbserver.models.dto.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateBookingRequest {

    private Long listingId;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guestsCount;

}