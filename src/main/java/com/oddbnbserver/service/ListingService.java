package com.oddbnbserver.service;

import com.oddbnbserver.models.Listing;
import com.oddbnbserver.models.Review;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.listing.*;
import com.oddbnbserver.models.dto.listing.HostSummary;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ListingService {

    private final ListingRepo listingRepo;
    private final UserRepo userRepo;

    public ListingService(ListingRepo listingRepo, UserRepo userRepo) {
        this.listingRepo = listingRepo;
        this.userRepo = userRepo;
    }

    // ======================================================
    // CREATE
    // ======================================================
    public ListingDetail create(CreateListingRequest dto) {

        Long userId = SecurityUtils.getCurrentUserId();

        User host = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (host.getRole() != User.Role.HOST &&
                host.getRole() != User.Role.ADMIN) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only hosts can create listings");
        }

        Listing listing = new Listing();

        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setPricePerNight(dto.getPricePerNight());
        listing.setLocation(dto.getLocation());

        listing.setLat(dto.getLat());
        listing.setLon(dto.getLon());

        listing.setBeds(dto.getBeds());
        listing.setBaths(dto.getBaths());
        listing.setCapacity(dto.getCapacity());
        listing.setSquareFeet(dto.getSquareFeet());
        listing.setAvailable(dto.isAvailable());

        listing.setHost(host);

        Listing saved = listingRepo.save(listing);

        return toDetail(saved);
    }

    // ======================================================
    // READ
    // ======================================================
    public List<ListingSummary> getAllListings() {
        return listingRepo.findAll()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public ListingDetail getListingDetail(Long id) {
        Listing listing = getListingEntity(id);
        return toDetail(listing);
    }

    // ======================================================
    // UPDATE (PATCH)
    // ======================================================
    public ListingDetail updateListing(Long id, UpdateListingRequest dto) {

        Listing listing = getListingEntity(id);
        assertOwnerOrAdmin(listing);

        if (dto.getTitle() != null)
            listing.setTitle(dto.getTitle());

        if (dto.getDescription() != null)
            listing.setDescription(dto.getDescription());

        if (dto.getPricePerNight() != null)
            listing.setPricePerNight(dto.getPricePerNight());

        if (dto.getLocation() != null)
            listing.setLocation(dto.getLocation());

        if (dto.getLat() != null)
            listing.setLat(dto.getLat());

        if (dto.getLon() != null)
            listing.setLon(dto.getLon());

        if (dto.getBeds() != null)
            listing.setBeds(dto.getBeds());

        if (dto.getBaths() != null)
            listing.setBaths(dto.getBaths());

        if (dto.getCapacity() != null)
            listing.setCapacity(dto.getCapacity());

        if (dto.getSquareFeet() != null)
            listing.setSquareFeet(dto.getSquareFeet());

        if (dto.getAvailable() != null)
            listing.setAvailable(dto.getAvailable());

        Listing saved = listingRepo.save(listing);

        return toDetail(saved);
    }

    // ======================================================
    // DELETE
    // ======================================================
    public void removeListing(Long id) {
        Listing listing = getListingEntity(id);
        assertOwnerOrAdmin(listing);
        listingRepo.delete(listing);
    }

    // ======================================================
    // PRIVATE HELPERS
    // ======================================================

    private Listing getListingEntity(Long id) {
        return listingRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"));
    }

    private void assertOwnerOrAdmin(Listing listing) {

        Long userId = SecurityUtils.getCurrentUserId();

        boolean isOwner = listing.getHost().getId().equals(userId);
        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    private ListingSummary toSummary(Listing listing) {

        ListingSummary s = new ListingSummary();

        s.setId(listing.getId());
        s.setTitle(listing.getTitle());
        s.setPricePerNight(listing.getPricePerNight());
        s.setBeds(listing.getBeds());
        s.setBaths(listing.getBaths());
        s.setCapacity(listing.getCapacity());

        List<Review> reviews = listing.getReviews();

        int count = reviews.size();
        s.setReviewCount(count);

        s.setRating(count == 0 ? null :
                reviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0));

        return s;
    }

    private ListingDetail toDetail(Listing listing) {

        ListingDetail d = new ListingDetail();

        d.setId(listing.getId());
        d.setTitle(listing.getTitle());
        d.setDescription(listing.getDescription());
        d.setPricePerNight(listing.getPricePerNight());
        d.setLocation(listing.getLocation());

        d.setLat(listing.getLat());
        d.setLon(listing.getLon());

        d.setBeds(listing.getBeds());
        d.setBaths(listing.getBaths());
        d.setSquareFeet(listing.getSquareFeet());
        d.setCapacity(listing.getCapacity());
        d.setAvailable(listing.isAvailable());

        User host = listing.getHost();
        if (host != null) {
            HostSummary hs = new HostSummary();
            hs.setId(host.getId());
            hs.setFirstName(host.getFirstName());
            hs.setLastName(host.getLastName());
            d.setHost(hs);
        }

        d.setImageUrls(
                listing.getImages()
                        .stream()
                        .map(img -> img.getImageUrl())
                        .toList()
        );

        List<Review> reviews = listing.getReviews();

        int count = reviews.size();
        d.setReviewCount(count);

        d.setRating(count == 0 ? null :
                reviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0));

        return d;
    }
}