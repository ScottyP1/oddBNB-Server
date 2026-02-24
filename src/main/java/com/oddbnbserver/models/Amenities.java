package com.oddbnbserver.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}