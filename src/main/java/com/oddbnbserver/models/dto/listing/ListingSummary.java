package com.oddbnbserver.models.dto.listing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingSummary {

    private Long id;
    private String title;
    private String city;
    private Double pricePerNight;
}
