package com.oddbnbserver.test;


import com.oddbnbserver.models.Favorite;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.FavoriteRepo;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.security.SecurityUtils;
import com.oddbnbserver.service.FavoriteService;
import com.oddbnbserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepo favoriteRepo;
    @Mock
    private UserService userService;
    @Mock
    private ListingRepo listingRepo;

    @InjectMocks
    private FavoriteService favoriteService;


    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("fName");
        user.setLastName("lName");
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
    void shouldReturnUsersFavorites() {
        Long userId = SecurityUtils.getRequiredUserId();

        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Treehouse");
        listing.setPricePerNight(200.0);
        listing.setHost(user);

        Favorite favorite = new Favorite();
        favorite.setId(1l);
        favorite.setUser(user);
        favorite.setListing(listing);

        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(favoriteRepo.findAllByUserId(userId)).thenReturn(List.of(favorite));

        var result = favoriteService.getFavoritesForCurrentUser();

        assertEquals(1, result.size());

    }

    @Test
    void shouldRemoveFavoriteIfAlreadyExists() {

        Long listingId = 1L;

        Listing listing = new Listing();
        listing.setId(listingId);

        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setListing(listing);

        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(favoriteRepo.findByUserIdAndListingId(user.getId(), listingId))
                .thenReturn(java.util.Optional.of(favorite));

        favoriteService.toggleFavorite(listingId);

        verify(favoriteRepo).delete(favorite);
    }

    @Test
    void shouldCreateFavoriteIfNotExists() {

        Long listingId = 1L;

        Listing listing = new Listing();
        listing.setId(listingId);
        listing.setTitle("Treehouse");

        when(userService.getCurrentUserEntity()).thenReturn(user);

        when(favoriteRepo.findByUserIdAndListingId(user.getId(), listingId))
                .thenReturn(java.util.Optional.empty());

        when(listingRepo.findById(listingId))
                .thenReturn(java.util.Optional.of(listing));

        favoriteService.toggleFavorite(listingId);

        verify(favoriteRepo).save(any(Favorite.class));
    }
}
