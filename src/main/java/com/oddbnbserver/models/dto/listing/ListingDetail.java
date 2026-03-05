package com.oddbnbserver.models.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
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

    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer reviewCount;
    private Double rating;
    private List<String> amenities;
    private List<String> imageUrls;
}