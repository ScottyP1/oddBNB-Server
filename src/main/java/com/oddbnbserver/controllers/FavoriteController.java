package com.oddbnbserver.controllers;

import com.oddbnbserver.models.Favorite;
import com.oddbnbserver.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // CREATE
    @PostMapping
    public Favorite create(@RequestBody Favorite favorite) {
        return favoriteService.create(favorite);
    }

    // READ
    @GetMapping("/{id}")
    public Favorite getReview(@PathVariable Long id) {
        return favoriteService.getFavorite(id);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
    }
}
