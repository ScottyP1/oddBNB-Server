package com.oddbnbserver.controllers;

import com.oddbnbserver.models.dto.listing.*;
import com.oddbnbserver.service.ListingService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public ListingDetail create(@RequestBody CreateListingRequest dto) {
        System.out.println("Controller");
        return listingService.create(dto);
    }

    // READ
    @GetMapping
    public List<ListingSummary> getAllListings() {
        return listingService.getAllListings();
    }

    // Get owned listings
    @GetMapping("/owned")
    public List<ListingSummary> getAllOwnedListings() {
        return listingService.getAllOwnedListings();
    }

    @GetMapping("/{id}")
    public ListingDetail getListing(@PathVariable Long id) {
        return listingService.getListingDetail(id);
    }

    // UPDATE
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public ListingDetail update(
            @PathVariable Long id,
            @RequestBody UpdateListingRequest dto) {

        return listingService.updateListing(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public void delete(@PathVariable Long id) {
        listingService.removeListing(id);
    }
}