package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    boolean existsByListingIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
            Long listingId,
            Booking.Status status,
            LocalDate newCheckOut,
            LocalDate newCheckIn
    );

    boolean existsByListingIdAndStatusAndCheckInLessThanAndCheckOutGreaterThanAndIdNot(
            Long listingId,
            Booking.Status status,
            LocalDate end,
            LocalDate start,
            Long bookingId
    );

    List<Booking> findByGuest_Id(Long guestId);

    List<Booking> findByListing_Id(Long listingId);

    List<Booking> findByListing_IdAndStatus(Long listingId, Booking.Status status);

    List<Booking> findByListing_Host_Id(Long hostId);
}
