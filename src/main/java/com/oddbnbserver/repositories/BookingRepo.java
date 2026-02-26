package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    boolean existsByListingIdAndCheckInLessThanAndCheckOutGreaterThan(
            Long listingId,
            LocalDate newCheckOut,
            LocalDate newCheckIn
    );

    boolean existsByListingIdAndCheckInLessThanAndCheckOutGreaterThanAndIdNot(Long listingId, LocalDate end, LocalDate start, Long bookingId);
}
