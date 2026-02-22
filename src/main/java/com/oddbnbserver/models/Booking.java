package com.oddbnbserver.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum Status {
        INACTIVE,
        ACTIVE,
        BOOKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guestsCount;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public Booking() {
    }

    public Booking(Long id, Listing listing, User guest, LocalDate checkIn, LocalDate checkOut, Integer guestsCount,
                   Double totalPrice, Status status) {
        this.id = id;
        this.listing = listing;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestsCount = guestsCount;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getGuestsCount() {
        return guestsCount;
    }

    public void setGuestsCount(Integer guestsCount) {
        this.guestsCount = guestsCount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
