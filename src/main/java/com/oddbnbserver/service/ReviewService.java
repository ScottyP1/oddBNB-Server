package com.oddbnbserver.service;

import com.oddbnbserver.models.Review;
import com.oddbnbserver.repositories.ReviewRepo;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepo reviewRepo;

    public ReviewService(ReviewRepo reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    // CREATE
    public Review create(Review newReview) {
        return reviewRepo.save(newReview);
    }

    // READ
    public Review getReview(Long id) {
        return reviewRepo.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    // UPDATE
    public Review updateReview(Review review) {
        Review existingReview = getReview(review.getId());

        existingReview.setComment(review.getComment());
        existingReview.setRating(review.getRating());

        return reviewRepo.save(existingReview);
    }

    // DELETE
    public void removeReview(Long id) {
        Review review = getReview(id);
        reviewRepo.delete(review);
    }
}
