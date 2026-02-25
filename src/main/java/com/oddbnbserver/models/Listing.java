package com.oddbnbserver.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Double pricePerNight;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    @Column(nullable = false)
    private Integer beds;

    @Column(nullable = false)
    private Integer baths;

    @Column(nullable = false)
    private Double squareFeet;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL)
    private Amenities amenities;

    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "listing")
    private List<Review> reviews;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListingImage> images;
}
