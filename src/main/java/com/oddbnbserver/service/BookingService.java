package com.oddbnbserver.service;


import com.oddbnbserver.models.Booking;
import com.oddbnbserver.repositories.BookingRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookingService {

    private final BookingRepo bookingRepo;


    public BookingService(BookingRepo bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    // CREATE
    public Booking create(Booking booking) {

        if (booking.getListing() == null ||
                booking.getCheckIn() == null ||
                booking.getCheckOut() == null) {

            throw new RuntimeException("Missing required booking data");
        }

        Long listingId = booking.getListing().getId();
        LocalDate newStart = booking.getCheckIn();
        LocalDate newEnd = booking.getCheckOut();

        if (!newStart.isBefore(newEnd)) {
            throw new RuntimeException("Invalid date range");
        }

        if (bookingRepo
                .existsByListingIdAndCheckInLessThanAndCheckOutGreaterThan(
                        listingId,
                        newEnd,
                        newStart
                )) {

            throw new RuntimeException("Listing is already booked for those dates");
        }

        return bookingRepo.save(booking);
    }

    // READ
    public Booking getBooking(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found."));
    }

    // UPDATE
    public Booking updateBooking(Long id, Booking booking) {
        Booking existingBooking = getBooking(id);

        existingBooking.setCheckIn(booking.getCheckIn());
        existingBooking.setCheckOut(booking.getCheckOut());
        existingBooking.setGuestsCount(booking.getGuestsCount());
        existingBooking.setTotalPrice(booking.getTotalPrice());

        return bookingRepo.save(existingBooking);
    }

    // DELETE
    public void removeBooking(Long id) {
        Booking booking = getBooking(id);

        bookingRepo.delete(booking);
    }

}
