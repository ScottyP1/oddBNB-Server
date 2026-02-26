package com.oddbnbserver.service;

import com.oddbnbserver.models.Booking;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.booking.BookingSummary;
import com.oddbnbserver.models.dto.booking.CreateBookingRequest;
import com.oddbnbserver.models.dto.booking.UpdateBookingRequest;
import com.oddbnbserver.repositories.BookingRepo;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepo bookingRepo;
    private final ListingRepo listingRepo;
    private final UserRepo userRepo;

    public BookingService(
            BookingRepo bookingRepo,
            ListingRepo listingRepo,
            UserRepo userRepo) {

        this.bookingRepo = bookingRepo;
        this.listingRepo = listingRepo;
        this.userRepo = userRepo;
    }

    // =============================
    // CREATE
    // =============================
    public BookingSummary create(CreateBookingRequest req) {

        Long userId = SecurityUtils.getCurrentUserId();

        Listing listing = listingRepo.findById(req.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        User guest = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate start = req.getCheckIn();
        LocalDate end = req.getCheckOut();

        validateDates(start, end);

        ensureAvailability(listing.getId(), start, end);

        long nights = ChronoUnit.DAYS.between(start, end);
        double price = nights * listing.getPricePerNight();

        Booking booking = new Booking();
        booking.setListing(listing);
        booking.setGuest(guest);
        booking.setCheckIn(start);
        booking.setCheckOut(end);
        booking.setGuestsCount(req.getGuestsCount());
        booking.setTotalPrice(price);
        booking.setStatus(Booking.Status.CONFIRMED);

        Booking saved = bookingRepo.save(booking);

        return toSummary(saved, nights, price, "Booking confirmed");
    }

    // =============================
    // READ
    // =============================
    public BookingSummary getBooking(Long id) {

        Booking booking = getBookingEntity(id);

        long nights = ChronoUnit.DAYS.between(
                booking.getCheckIn(),
                booking.getCheckOut()
        );

        return toSummary(
                booking,
                nights,
                booking.getTotalPrice(),
                "Booking retrieved"
        );
    }

    public List<BookingSummary> getAllBookings() {

        return bookingRepo.findAll()
                .stream()
                .map(b -> {
                    long nights = ChronoUnit.DAYS.between(
                            b.getCheckIn(),
                            b.getCheckOut()
                    );
                    return toSummary(
                            b,
                            nights,
                            b.getTotalPrice(),
                            null
                    );
                })
                .toList();
    }

    // =============================
    // UPDATE
    // =============================
    public BookingSummary updateBooking(Long id,
                                        UpdateBookingRequest req) {

        Booking existing = getBookingEntity(id); // ✅ FIXED

        Long userId = SecurityUtils.getCurrentUserId();

        if (!existing.getGuest().getId().equals(userId)
                && !SecurityUtils.isAdmin()) {
            throw new RuntimeException("Forbidden");
        }

        LocalDate start = req.getCheckIn();
        LocalDate end = req.getCheckOut();

        validateDates(start, end);

        Listing listing = existing.getListing();

        ensureAvailabilityForUpdate(
                listing.getId(),
                start,
                end,
                existing.getId()
        );

        long nights = ChronoUnit.DAYS.between(start, end);
        double price = nights * listing.getPricePerNight();

        existing.setCheckIn(start);
        existing.setCheckOut(end);
        existing.setGuestsCount(req.getGuestsCount());
        existing.setTotalPrice(price);

        Booking saved = bookingRepo.save(existing);

        return toSummary(saved, nights, price, "Booking updated");
    }

    // =============================
    // DELETE
    // =============================
    public void removeBooking(Long id) {
        bookingRepo.delete(getBookingEntity(id)); // ✅ FIXED
    }

    // =============================
    // PRIVATE HELPERS
    // =============================
    private Booking getBookingEntity(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
    
    private void validateDates(LocalDate start, LocalDate end) {
        if (!start.isBefore(end)) {
            throw new RuntimeException("Invalid date range");
        }
    }

    private void ensureAvailability(Long listingId,
                                    LocalDate start,
                                    LocalDate end) {

        boolean exists = bookingRepo
                .existsByListingIdAndCheckInLessThanAndCheckOutGreaterThan(
                        listingId, end, start);

        if (exists) {
            throw new RuntimeException("Listing already booked");
        }
    }

    private void ensureAvailabilityForUpdate(Long listingId,
                                             LocalDate start,
                                             LocalDate end,
                                             Long bookingId) {

        boolean exists = bookingRepo
                .existsByListingIdAndCheckInLessThanAndCheckOutGreaterThanAndIdNot(
                        listingId, end, start, bookingId);

        if (exists) {
            throw new RuntimeException("Listing already booked");
        }
    }

    private BookingSummary toSummary(Booking booking,
                                     long nights,
                                     double price,
                                     String message) {

        BookingSummary res = new BookingSummary();

        res.setBookingId(booking.getId());
        res.setListingId(booking.getListing().getId());
        res.setCheckIn(booking.getCheckIn());
        res.setCheckOut(booking.getCheckOut());
        res.setGuestsCount(booking.getGuestsCount());
        res.setNights(nights);
        res.setTotalPrice(price);
        res.setStatus(booking.getStatus().name());
        res.setMessage(message);

        return res;
    }
}