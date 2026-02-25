package com.oddbnbserver.service;

import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class ListingService {
    private final ListingRepo listingRepo;
    private final UserRepo userRepo;

    public ListingService(ListingRepo listingRepo, UserRepo userRepo) {
        this.listingRepo = listingRepo;
        this.userRepo = userRepo;
    }

    // CREATE
    public Listing create(Listing newListing) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        User host = userRepo.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        newListing.setHost(host);

        return listingRepo.save(newListing);
    }

    // READ
    public Listing getListing(Long id) {
        return listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    // UPDATE
    public Listing updateListing(Long id, Listing listing) {

        Listing existing = getListing(id);

        Long currentUserId = SecurityUtils.getCurrentUserId();

        boolean isOwner =
                existing.getHost().getId().equals(currentUserId);

        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Forbidden");
        }

        // Apply updates
        existing.setDescription(listing.getDescription());
        existing.setAvailable(listing.isAvailable());
        existing.setBaths(listing.getBaths());
        existing.setBeds(listing.getBeds());
        existing.setLat(listing.getLat());
        existing.setLon(listing.getLon());
        existing.setLocation(listing.getLocation());
        existing.setPricePerNight(listing.getPricePerNight());
        existing.setSquareFeet(listing.getSquareFeet());
        existing.setTitle(listing.getTitle());
        existing.setCapacity(listing.getCapacity());
        existing.setImages(listing.getImages());

        return listingRepo.save(existing);
    }

    // DELETE
    public void removeListing(Long id) {

        Listing listing = getListing(id);

        Long currentUserId = SecurityUtils.getCurrentUserId();

        boolean isOwner =
                listing.getHost().getId().equals(currentUserId);

        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Forbidden");
        }

        listingRepo.delete(listing);
    }
}
