package com.oddbnbserver.service;

import com.oddbnbserver.models.Listing;
import com.oddbnbserver.repositories.ListingRepo;
import org.springframework.stereotype.Service;

@Service
public class ListingService {
    private final ListingRepo listingRepo;

    public ListingService(ListingRepo listingRepo) {
        this.listingRepo = listingRepo;
    }

    // CREATE
    public Listing create(Listing newListing) {
        return listingRepo.save(newListing);
    }

    // READ
    public Listing getListing(Long id) {
        return listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    // UPDATE
    public Listing updateListing(Long id, Listing listing) {
        Listing existingListing = getListing(id);

        existingListing.setDescription(listing.getDescription());
        existingListing.setAvailable(listing.isAvailable());
        existingListing.setBaths(listing.getBaths());
        existingListing.setBeds(listing.getBeds());
        existingListing.setLat(listing.getLat());
        existingListing.setLon(listing.getLon());
        existingListing.setLocation(listing.getLocation());
        existingListing.setPrice_per_night(listing.getPrice_per_night());
        existingListing.setSquare_feet(listing.getSquare_feet());
        existingListing.setTitle(listing.getTitle());
        existingListing.setCapacity(listing.getCapacity());


        return listingRepo.save(existingListing);
    }

    // DELETE
    public void removeListing(Long id) {
        Listing listing = getListing(id);
        listingRepo.delete(listing);
    }
}
