package com.oddbnbserver.models;


import jakarta.persistence.*;

import java.util.List;

@Entity
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
    private boolean availability;

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
    
    public Listing() {
    }

    public Listing(String title, String description, Double price_per_night, String location, Double lat, Double lon,
                   Integer beds, Integer baths, Double square_feet, Integer capacity, boolean availability) {
        this.title = title;
        this.description = description;
        this.pricePerNight = price_per_night;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.beds = beds;
        this.baths = baths;
        this.squareFeet = square_feet;
        this.capacity = capacity;
        this.availability = availability;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice_per_night() {
        return pricePerNight;
    }

    public void setPrice_per_night(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public Integer getBaths() {
        return baths;
    }

    public void setBaths(Integer baths) {
        this.baths = baths;
    }

    public Double getSquare_feet() {
        return squareFeet;
    }

    public void setSquare_feet(Double squareFeet) {
        this.squareFeet = squareFeet;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
