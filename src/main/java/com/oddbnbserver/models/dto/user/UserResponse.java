package com.oddbnbserver.models.dto.user;


import com.oddbnbserver.models.dto.listing.ListingSummary;
import com.oddbnbserver.models.dto.review.ReviewSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;

    private List<ListingSummary> hostedListings;
    private List<ReviewSummary> reviewsWritten;
}