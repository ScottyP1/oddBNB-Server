package com.oddbnbserver.controllers;

import com.oddbnbserver.models.Booking;
import com.oddbnbserver.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public Booking create(@RequestBody Booking booking) {
        return bookingService.create(booking);
    }

    // READ
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public Booking getReview(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    // UPDATE (PATCH = partial update)
    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public Booking updateReview(@PathVariable Long id,
                                @RequestBody Booking booking) {
        return bookingService.updateBooking(id, booking);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public void deleteReview(@PathVariable Long id) {
        bookingService.removeBooking(id);
    }
}
