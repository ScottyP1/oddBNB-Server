package com.oddbnbserver.test;

import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.listing.CreateListingRequest;
import com.oddbnbserver.models.dto.listing.ListingDetail;
import com.oddbnbserver.models.dto.listing.ListingSummary;
import com.oddbnbserver.models.dto.listing.UpdateListingRequest;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import com.oddbnbserver.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListingServiceTest {

    @Mock
    private ListingRepo listingRepo;
    @Mock
    private UserRepo userRepo;
    
    @InjectMocks
    private ListingService listingService;

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
    void shouldReturnOwnedListings() {

        Long userId = SecurityUtils.getRequiredUserId();

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Treehouse");
        listing.setPricePerNight(200.0);
        listing.setBeds(2);
        listing.setBaths(1);
        listing.setCapacity(4);
        listing.setHost(user);

        when(listingRepo.findByHostId(userId)).thenReturn(List.of(listing));

        var result = listingService.getAllOwnedListings();

        assertEquals(1, result.size());
        assertEquals("Treehouse", result.get(0).getTitle());
    }

    @Test
    void shouldReturnListings() {

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Cabin");
        listing.setPricePerNight(150.0);
        listing.setBeds(2);
        listing.setBaths(1);
        listing.setCapacity(4);

        when(listingRepo.findAll()).thenReturn(List.of(listing));

        var result = listingService.getAllListings();

        assertEquals(1, result.size());
        assertEquals("Cabin", result.get(0).getTitle());
    }

    @Test
    void shouldReturnListingDetail() {

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Cabin");
        listing.setHost(user);

        when(listingRepo.findById(1L)).thenReturn(Optional.of(listing));

        var result = listingService.getListingDetail(1L);

        assertEquals("Cabin", result.getTitle());
    }

    @Test
    void shouldRemoveListingIfOwner() {

        Long userId = SecurityUtils.getCurrentUserId();

        User host = new User();
        host.setId(userId);
        host.setRole(User.Role.HOST);

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setHost(host);

        when(listingRepo.findById(1L))
                .thenReturn(Optional.of(listing));

        listingService.removeListing(1L);

        verify(listingRepo).delete(listing);
    }

    @Test
    void shouldCreateListing() {

        Long userId = SecurityUtils.getCurrentUserId();

        User host = new User();
        host.setId(userId);
        host.setRole(User.Role.HOST);

        CreateListingRequest request = new CreateListingRequest();
        request.setTitle("Cabin");
        request.setDescription("Nice stay");
        request.setPricePerNight(200.0);
        request.setLocation("Colorado");
        request.setAmenities(List.of("wifi", "kitchen"));

        when(userRepo.findById(userId)).thenReturn(Optional.of(host));
        when(listingRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ListingDetail result = listingService.create(request);

        assertEquals("Cabin", result.getTitle());
        assertEquals(true, result.getAmenities().contains("wifi"));
    }

    @Test
    void shouldThrowIfUserNotHost() {

        Long userId = SecurityUtils.getCurrentUserId();

        User user = new User();
        user.setId(userId);
        user.setRole(User.Role.GUEST);

        CreateListingRequest request = new CreateListingRequest();
        request.setTitle("Cabin");

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class,
                () -> listingService.create(request));
    }

    @Test
    void shouldThrowIfUserNotOwner() {

        Listing listing = new Listing();

        User host = new User();
        host.setId(999L);

        listing.setHost(host);

        when(listingRepo.findById(1L)).thenReturn(Optional.of(listing));

        assertThrows(ResponseStatusException.class,
                () -> listingService.updateListing(1L, new UpdateListingRequest()));
    }

    @Test
    void shouldUpdateListing() {
        Long userId = SecurityUtils.getCurrentUserId();

        User host = new User();
        host.setId(userId);
        host.setRole(User.Role.HOST);

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setHost(host);
        listing.setTitle("Initial Title");

        UpdateListingRequest updatedListing = new UpdateListingRequest();
        updatedListing.setTitle("Updated Title");


        when(listingRepo.findById(1l)).thenReturn(Optional.of(listing));
        when(listingRepo.save(listing)).thenReturn(listing);

        ListingDetail result = listingService.updateListing(1l, updatedListing);

        assertEquals("Updated Title", result.getTitle());

        verify(listingRepo).save(listing);
    }

    @Test
    void shouldReturnAllListings() {

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Treehouse");
        listing.setPricePerNight(200.0);
        listing.setBeds(2);
        listing.setBaths(1);
        listing.setCapacity(4);

        listing.setReviews(new ArrayList<>());
        listing.setImages(new ArrayList<>());

        when(listingRepo.findAll()).thenReturn(List.of(listing));

        List<ListingSummary> listings = listingService.getAllListings();

        assertEquals(1, listings.size());
        assertEquals(1L, listings.get(0).getId());
        assertEquals("Treehouse", listings.get(0).getTitle());
    }


}