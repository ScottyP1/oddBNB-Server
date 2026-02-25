package com.oddbnbserver.models.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListingDetail {

    private Long id;
    private String title;
    private String description;
    private Double pricePerNight;
    private String location;

    private Double lat;
    private Double lon;

    private Integer beds;
    private Integer baths;
    private Double squareFeet;
    private Integer capacity;

    private boolean available;

    private HostSummary host;

    private List<String> imageUrls = List.of();

    private Integer reviewCount;
    private Double rating;
}