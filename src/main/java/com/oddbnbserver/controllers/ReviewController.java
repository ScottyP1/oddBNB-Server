package com.oddbnbserver.controllers;

import com.oddbnbserver.models.Review;
import com.oddbnbserver.service.ReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // CREATE
    @PostMapping
    public Review create(@RequestBody Review review) {
        return reviewService.create(review);
    }

    // READ
    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    // UPDATE (PATCH = partial update)
    @PatchMapping("/{id}")
    public Review updateReview(@PathVariable Long id,
                               @RequestBody Review review) {
        return reviewService.updateReview(id, review);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.removeReview(id);
    }
}

