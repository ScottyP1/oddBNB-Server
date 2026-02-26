package com.oddbnbserver.models.dto.listing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingSummary {

    private Long id;
    private String title;
    private Double pricePerNight;
    private Integer beds;
    private Integer baths;
    private Integer capacity;

    private Integer reviewCount;
    private Double rating;
    private String thumbnailUrl;
}