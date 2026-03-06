package com.oddbnbserver.test;

import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.Review;
import com.oddbnbserver.repositories.ReviewRepo;
import com.oddbnbserver.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    ReviewRepo reviewRepo;

    @InjectMocks
    ReviewService reviewService;

    private Review review;
    private Listing listing;

    @BeforeEach
    void setUp() {
        listing = new Listing();
        listing.setId(1l);

        review = new Review();
        review.setId(1l);
        review.setComment("test comment");
        review.setListing(listing);
        review.setRating(4.5);
    }

    @Test
    void shouldReturnReviewForListing() {

        when(reviewRepo.findById(1l)).thenReturn(Optional.of(review));

        var result = reviewService.getReview(1l);

        assertEquals(1l, result.getId());
    }

    @Test
    void shouldCreateReview() {
        Review newReview = new Review();
        newReview.setId(2L);

        when(reviewRepo.save(newReview)).thenReturn(newReview);

        Review result = reviewService.create(newReview);

        assertEquals(2L, result.getId());
        verify(reviewRepo).save(newReview);
    }

    @Test
    void shouldUpdateReview() {

        Review newReview = new Review();
        newReview.setComment("Updated comment");

        when(reviewRepo.findById(1L))
                .thenReturn(Optional.of(review));

        when(reviewRepo.save(review))
                .thenReturn(review);

        reviewService.updateReview(1L, newReview);

        assertEquals("Updated comment", review.getComment());

        verify(reviewRepo).save(review);
    }
}