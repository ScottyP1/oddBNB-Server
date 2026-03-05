package com.oddbnbserver.controllers;

import com.oddbnbserver.models.dto.favorite.FavoriteSummary;
import com.oddbnbserver.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<FavoriteSummary> getFavorites() {
        return favoriteService.getFavoritesForCurrentUser();
    }

    @PostMapping("/{listingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFavorite(@PathVariable Long listingId) {
        favoriteService.toggleFavorite(listingId);
    }

}