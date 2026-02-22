package com.oddbnbserver.models;

import jakarta.persistence.*;

@Entity
@Table(name = "amenities")
public class Amenities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "listing_id", nullable = false, unique = true)
    private Listing listing;

    @Column(nullable = false)
    private boolean desertView;

    @Column(nullable = false)
    private boolean mountainView;

    @Column(nullable = false)
    private boolean valleyView;

    @Column(nullable = false)
    private boolean wifi;

    @Column(nullable = false)
    private boolean tv;

    @Column(nullable = false)
    private boolean kitchen;

    @Column(nullable = false)
    private boolean washer;

    @Column(nullable = false)
    private boolean dryer;

    @Column(nullable = false)
    private boolean petsAllowed;

    @Column(nullable = false)
    private boolean smokeAlarm;

    public Amenities() {
    }

    public Amenities(Long id, Long listing_amenities, boolean desertView, boolean mountainView, boolean valleyView,
                     boolean wifi, boolean tv, boolean kitchen, boolean washer, boolean dryer, boolean petsAllowed,
                     boolean smokeAlarm) {
        this.id = id;
        this.desertView = desertView;
        this.mountainView = mountainView;
        this.valleyView = valleyView;
        this.wifi = wifi;
        this.tv = tv;
        this.kitchen = kitchen;
        this.washer = washer;
        this.dryer = dryer;
        this.petsAllowed = petsAllowed;
        this.smokeAlarm = smokeAlarm;
    }

    public Long getId() {
        return id;
    }


    public boolean isDesert_view() {
        return desertView;
    }

    public void setDesert_view(boolean desertView) {
        this.desertView = desertView;
    }

    public boolean isMountain_view() {
        return mountainView;
    }

    public void setMountain_view(boolean mountainView) {
        this.mountainView = mountainView;
    }

    public boolean isValley_view() {
        return valleyView;
    }

    public void setValley_view(boolean valleyView) {
        this.valleyView = valleyView;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isTv() {
        return tv;
    }

    public void setTv(boolean tv) {
        this.tv = tv;
    }

    public boolean isKitchen() {
        return kitchen;
    }

    public void setKitchen(boolean kitchen) {
        this.kitchen = kitchen;
    }

    public boolean isWasher() {
        return washer;
    }

    public void setWasher(boolean washer) {
        this.washer = washer;
    }

    public boolean isDryer() {
        return dryer;
    }

    public void setDryer(boolean dryer) {
        this.dryer = dryer;
    }

    public boolean isPets_allowed() {
        return petsAllowed;
    }

    public void setPets_allowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public boolean isSmoke_alarm() {
        return smokeAlarm;
    }

    public void setSmoke_alarm(boolean smokeAlarm) {
        this.smokeAlarm = smokeAlarm;
    }

}
