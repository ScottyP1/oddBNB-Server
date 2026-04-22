package com.oddbnbserver.service;

import com.oddbnbserver.models.Booking;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.booking.BookingSummary;
import com.oddbnbserver.models.dto.booking.BookingAvailability;
import com.oddbnbserver.models.dto.booking.CreateBookingRequest;
import com.oddbnbserver.models.dto.booking.BookingStatusUpdateRequest;
import com.oddbnbserver.models.dto.booking.UpdateBookingRequest;
import com.oddbnbserver.repositories.BookingRepo;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public BookingSummary create(CreateBookingRequest req) {

        Long userId = SecurityUtils.getRequiredUserId();

        Listing listing = listingRepo.findById(req.getListingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        User guest = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (listing.getHost().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hosts cannot book their own listing"
            );
        }

        LocalDate start = req.getCheckIn();
        LocalDate end = req.getCheckOut();

        validateBookingRequest(start, end, req.getGuestsCount(), listing);

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
        booking.setStatus(Booking.Status.PENDING);

        Booking saved = bookingRepo.save(booking);

        return toSummary(saved, nights, price, "Booking pending");
    }

    public BookingSummary getBooking(Long id) {

        Booking booking = getBookingEntity(id);
        assertBookingParticipantOrAdmin(booking);

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

    public List<BookingSummary> getBookingsForUser(Long userId) {
        Long currentUserId = SecurityUtils.getRequiredUserId();

        if (!currentUserId.equals(userId) && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to view these bookings"
            );
        }

        List<Booking> bookings = bookingRepo.findByGuest_Id(userId);

        return bookings.stream()
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


    public List<BookingSummary> getCurrentUserBookings() {

        Long userId = SecurityUtils.getRequiredUserId();

        return getBookingsForUser(userId);
    }

    public List<BookingSummary> getBookingsForListing(Long id) {
        Listing listing = listingRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        assertListingHostOrAdmin(listing);

        List<Booking> bookings = bookingRepo.findByListing_Id(id);

        return bookings.stream()
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

    public List<BookingSummary> getCurrentHostBookings() {
        Long userId = SecurityUtils.getRequiredUserId();

        if (SecurityUtils.isAdmin()) {
            return getAllBookings();
        }

        return bookingRepo.findByListing_Host_Id(userId)
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

    public List<BookingAvailability> getPublicAvailabilityForListing(Long listingId) {
        listingRepo.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        return bookingRepo.findByListing_IdAndStatus(listingId, Booking.Status.CONFIRMED)
                .stream()
                .map(this::toAvailability)
                .toList();
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

    public BookingSummary updateBooking(Long id, UpdateBookingRequest req) {

        Long userId = SecurityUtils.getRequiredUserId();
        Booking existing = getBookingEntity(id);

        boolean isGuest = existing.getGuest().getId().equals(userId);
        boolean isHost = existing.getListing().getHost().getId().equals(userId);

        if (!isGuest && !isHost && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to update this booking"
            );
        }

        LocalDate start = req.getCheckIn() != null
                ? req.getCheckIn()
                : existing.getCheckIn();

        LocalDate end = req.getCheckOut() != null
                ? req.getCheckOut()
                : existing.getCheckOut();

        Integer guests = req.getGuestsCount() != null
                ? req.getGuestsCount()
                : existing.getGuestsCount();

        Listing listing = existing.getListing();

        validateBookingRequest(start, end, guests, listing);

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
        existing.setGuestsCount(guests);
        existing.setTotalPrice(price);

        Booking saved = bookingRepo.save(existing);

        return toSummary(saved, nights, price, "Booking updated");
    }

    public BookingSummary updateBookingStatus(Long id, BookingStatusUpdateRequest req) {

        Booking booking = getBookingEntity(id);
        assertListingHostOrAdmin(booking.getListing());

        Booking.Status nextStatus = parseHostDecision(req.getStatus());
        booking.setStatus(nextStatus);

        Booking saved = bookingRepo.save(booking);
        long nights = ChronoUnit.DAYS.between(
                saved.getCheckIn(),
                saved.getCheckOut()
        );

        String message = nextStatus == Booking.Status.CONFIRMED
                ? "Booking confirmed"
                : "Booking declined";

        return toSummary(saved, nights, saved.getTotalPrice(), message);
    }


    public void removeBooking(Long id) {

        Long userId = SecurityUtils.getRequiredUserId();

        Booking booking = getBookingEntity(id);

        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isHost = booking.getListing().getHost().getId().equals(userId);

        if (!isGuest && !isHost && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this booking"
            );
        }

        bookingRepo.delete(booking);
    }

    private Booking getBookingEntity(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"));
    }

    private void validateBookingRequest(LocalDate start,
                                        LocalDate end,
                                        Integer guestsCount,
                                        Listing listing) {
        if (start == null || end == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in and check-out are required"
            );
        }

        if (!start.isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date range"
            );
        }

        if (guestsCount == null || guestsCount < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guests count must be at least 1"
            );
        }

        if (listing.getCapacity() != null && guestsCount > listing.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guests count exceeds listing capacity"
            );
        }
    }

    private void ensureAvailability(Long listingId,
                                    LocalDate start,
                                    LocalDate end) {

        boolean exists = bookingRepo
                .existsByListingIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        listingId, Booking.Status.CONFIRMED, end, start);

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Listing already booked for selected dates"
            );
        }
    }

    private void ensureAvailabilityForUpdate(Long listingId,
                                             LocalDate start,
                                             LocalDate end,
                                             Long bookingId) {

        boolean exists = bookingRepo
                .existsByListingIdAndStatusAndCheckInLessThanAndCheckOutGreaterThanAndIdNot(
                        listingId, Booking.Status.CONFIRMED, end, start, bookingId);

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Listing already booked for selected dates"
            );
        }
    }

    private void assertBookingParticipantOrAdmin(Booking booking) {
        Long userId = SecurityUtils.getRequiredUserId();

        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isHost = booking.getListing().getHost().getId().equals(userId);

        if (!isGuest && !isHost && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to view this booking"
            );
        }
    }

    private void assertListingHostOrAdmin(Listing listing) {
        Long userId = SecurityUtils.getRequiredUserId();

        boolean isHost = listing.getHost().getId().equals(userId);

        if (!isHost && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to manage this listing's bookings"
            );
        }
    }

    private Booking.Status parseHostDecision(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        Booking.Status status;
        try {
            status = Booking.Status.valueOf(rawStatus.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid booking status"
            );
        }

        if (status != Booking.Status.CONFIRMED && status != Booking.Status.DECLINED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hosts can only confirm or decline bookings"
            );
        }

        return status;
    }

    private BookingAvailability toAvailability(Booking booking) {
        BookingAvailability availability = new BookingAvailability();
        availability.setCheckIn(booking.getCheckIn());
        availability.setCheckOut(booking.getCheckOut());
        return availability;
    }

    private BookingSummary toSummary(Booking booking,
                                     long nights,
                                     double price,
                                     String message) {

        BookingSummary res = new BookingSummary();

        res.setBookingId(booking.getId());
        res.setListingId(booking.getListing().getId());
        res.setGuestId(booking.getGuest().getId());
        res.setCheckIn(booking.getCheckIn());
        res.setCheckOut(booking.getCheckOut());
        res.setGuestsCount(booking.getGuestsCount());
        res.setNights(nights);
        res.setTotalPrice(price);
        res.setStatus(booking.getStatus().name());
        res.setMessage(message);

        Listing listing = booking.getListing();

        res.setTitle(listing.getTitle());
        res.setGuestFirstName(booking.getGuest().getFirstName());
        res.setGuestLastName(booking.getGuest().getLastName());

        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            res.setImageUrl(listing.getImages().getFirst().getImageUrl());
        }

        return res;
    }
}
