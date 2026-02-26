package com.oddbnbserver.models.dto.booking;

import com.oddbnbserver.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateBookingRequest {

    private Long listingId;

    private User guest;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guestsCount;

    private Double totalPrice;
}
