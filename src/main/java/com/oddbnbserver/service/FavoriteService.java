package com.oddbnbserver.service;

import com.oddbnbserver.models.Favorite;
import com.oddbnbserver.repositories.FavoriteRepo;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {
    private final FavoriteRepo favoriteRepo;

    public FavoriteService(FavoriteRepo favoriteRepo) {
        this.favoriteRepo = favoriteRepo;
    }

    // CREATE
    public Favorite create(Favorite newFavorite) {

        Long userId = newFavorite.getUser().getId();
        Long listingId = newFavorite.getListing().getId();

        if (favoriteRepo.existsByUserIdAndListingId(userId, listingId)) {
            throw new RuntimeException("Already favorited");
        }

        return favoriteRepo.save(newFavorite);
    }

    // READ
    public Favorite getFavorite(Long id) {
        return favoriteRepo.findById(id).orElseThrow(() -> new RuntimeException("Favorite not found"));
    }

//    // UPDATE
//    public Favorite updateFavorite(Favorite favorite) {
//        Favorite existingFavorite = getFavorite(favorite.getId());
//
//
//        return favoriteRepo.save(existingFavorite);
//    }

    // DELETE
    public void removeFavorite(Long id) {
        Favorite listing = getFavorite(id);
        favoriteRepo.delete(listing);
    }
}
