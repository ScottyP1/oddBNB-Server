package com.oddbnbserver.controllers;

import com.oddbnbserver.models.Listing;
import com.oddbnbserver.service.ListingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public Listing create(@RequestBody Listing listing) {
        return listingService.create(listing);
    }

    // READ
    @GetMapping("/{id}")
    public Listing getReview(@PathVariable Long id) {
        return listingService.getListing(id);
    }

    // UPDATE (PATCH = partial update)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public Listing updateReview(@PathVariable Long id,
                                @RequestBody Listing listing) {
        return listingService.updateListing(id, listing);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public void deleteReview(@PathVariable Long id) {
        listingService.removeListing(id);
    }

}
