package com.oddbnbserver.models.dto.listing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateListingRequest {

    private String title;
    private String description;
    private Double pricePerNight;
    private String location;

    private Double lat = 0.0;
    private Double lon = 0.0;

    private Integer beds;
    private Integer baths;
    private Integer capacity;
    private Double squareFeet;

    private boolean available;
}