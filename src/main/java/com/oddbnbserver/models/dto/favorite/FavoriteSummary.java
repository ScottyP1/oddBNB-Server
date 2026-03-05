package com.oddbnbserver.models.dto.favorite;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteSummary {

    private Long favoriteId;
    private Long listingId;

    private String title;
    private String imageUrl;
    private String location;
    private Double price;

}