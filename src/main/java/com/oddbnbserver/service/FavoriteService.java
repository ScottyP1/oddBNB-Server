package com.oddbnbserver.service;

import com.oddbnbserver.models.Favorite;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.favorite.FavoriteSummary;
import com.oddbnbserver.repositories.FavoriteRepo;
import com.oddbnbserver.repositories.ListingRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepo favoriteRepo;
    private final ListingRepo listingRepo;
    private final UserService userService;

    public FavoriteService(
            FavoriteRepo favoriteRepo,
            ListingRepo listingRepo,
            UserService userService
    ) {
        this.favoriteRepo = favoriteRepo;
        this.listingRepo = listingRepo;
        this.userService = userService;
    }


    public List<FavoriteSummary> getFavoritesForCurrentUser() {

        User user = userService.getCurrentUserEntity();

        return favoriteRepo.findAllByUserId(user.getId())
                .stream()
                .map(f -> {

                    Listing listing = f.getListing();

                    String imageUrl = listing.getImages().isEmpty()
                            ? null
                            : listing.getImages().getFirst().getImageUrl();

                    return new FavoriteSummary(
                            f.getId(),
                            listing.getId(),
                            listing.getTitle(),
                            imageUrl,
                            listing.getLocation(),
                            listing.getPricePerNight()
                    );
                })
                .toList();
    }

    public void toggleFavorite(Long listingId) {

        User user = userService.getCurrentUserEntity();

        var existing = favoriteRepo.findByUserIdAndListingId(
                user.getId(),
                listingId
        );

        if (existing.isPresent()) {
            favoriteRepo.delete(existing.get());
            return;
        }
        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setListing(listing);

        favoriteRepo.save(favorite);
    }
}