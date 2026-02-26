package com.oddbnbserver.models.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class UpdateListingRequest {

    private String title;
    private String description;
    private Double pricePerNight;
    private String location;

    private Double lat;
    private Double lon;

    private Integer beds;
    private Integer baths;
    private Integer capacity;
    private Double squareFeet;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Boolean available;
}