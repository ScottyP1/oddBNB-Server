package com.oddbnbserver.models.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSummary {

    private Long id;
    private int rating;
    private String comment;
}