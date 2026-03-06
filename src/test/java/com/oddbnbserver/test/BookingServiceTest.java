package com.oddbnbserver.test;


import com.oddbnbserver.models.Booking;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.booking.CreateBookingRequest;
import com.oddbnbserver.models.dto.booking.UpdateBookingRequest;
import com.oddbnbserver.repositories.BookingRepo;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import com.oddbnbserver.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepo bookingRepo;
    @Mock
    ListingRepo listingRepo;

    @Mock
    UserRepo userRepo;

    @InjectMocks
    private BookingService bookingService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setFirstName("Cody");
        user.setLastName("Scott");
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setRole(User.Role.ADMIN);

        var auth = new UsernamePasswordAuthenticationToken(
                1L,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldReturnBookingsForUser() {

        Long userId = SecurityUtils.getRequiredUserId();

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Treehouse");
        listing.setPricePerNight(200.0);
        listing.setHost(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setListing(listing);
        booking.setGuest(user);
        booking.setGuestsCount(3);
        booking.setCheckIn(LocalDate.of(2026, 12, 12));
        booking.setCheckOut(LocalDate.of(2026, 12, 15));
        booking.setTotalPrice(120.0);

        CreateBookingRequest newBooking = new CreateBookingRequest();
        newBooking.setListingId(1L);
        newBooking.setGuestsCount(3);
        newBooking.setCheckIn(LocalDate.of(2026, 12, 12));
        newBooking.setCheckOut(LocalDate.of(2026, 12, 15));

        when(listingRepo.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepo.save(any())).thenReturn(booking);
        when(bookingRepo.findByGuest_Id(userId)).thenReturn(List.of(booking));

        bookingService.create(newBooking);

        var result = bookingService.getCurrentUserBookings();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getBookingId());
    }

    @Test
    void shouldRemoveBooking() {

        Long userId = SecurityUtils.getRequiredUserId();

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setHost(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setGuest(user);
        booking.setListing(listing);

        when(bookingRepo.findById(1L))
                .thenReturn(Optional.of(booking));

        bookingService.removeBooking(1L);

        verify(bookingRepo).delete(booking);
    }

    @Test
    void shouldUpdateBooking() {

        Long userId = SecurityUtils.getRequiredUserId();

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setHost(user);
        listing.setPricePerNight(200.0);
        
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setGuest(user);
        booking.setListing(listing);
        booking.setGuestsCount(2);

        UpdateBookingRequest updateRequest = new UpdateBookingRequest();
        updateRequest.setGuestsCount(4);
        updateRequest.setCheckIn(LocalDate.of(2026, 12, 20));
        updateRequest.setCheckOut(LocalDate.of(2026, 12, 25));

        when(bookingRepo.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepo.save(any()))
                .thenReturn(booking);

        bookingService.updateBooking(1L, updateRequest);

        assertEquals(4, booking.getGuestsCount());
        assertEquals(LocalDate.of(2026, 12, 20), booking.getCheckIn());
        assertEquals(LocalDate.of(2026, 12, 25), booking.getCheckOut());

        verify(bookingRepo).save(booking);
    }
}