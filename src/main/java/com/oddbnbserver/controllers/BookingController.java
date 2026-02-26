package com.oddbnbserver.controllers;

import com.oddbnbserver.models.dto.booking.BookingSummary;
import com.oddbnbserver.models.dto.booking.CreateBookingRequest;
import com.oddbnbserver.models.dto.booking.UpdateBookingRequest;
import com.oddbnbserver.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public BookingSummary create(@RequestBody CreateBookingRequest req) {
        return bookingService.create(req);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BookingSummary getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public List<BookingSummary> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public BookingSummary updateBooking(
            @PathVariable Long id,
            @RequestBody UpdateBookingRequest bookingUpdate) {

        return bookingService.updateBooking(id, bookingUpdate);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.removeBooking(id);
    }
}