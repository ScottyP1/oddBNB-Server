package com.oddbnbserver.service;

import com.oddbnbserver.models.*;
import com.oddbnbserver.models.dto.listing.*;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepo listingRepo;
    private final UserRepo userRepo;

    public ListingService(ListingRepo listingRepo, UserRepo userRepo) {
        this.listingRepo = listingRepo;
        this.userRepo = userRepo;
    }

    // =====================================================
    // CREATE
    // =====================================================

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

        listing.setLat(dto.getLat() != null ? dto.getLat() : 0.0);
        listing.setLon(dto.getLon() != null ? dto.getLon() : 0.0);

        listing.setBeds(dto.getBeds());
        listing.setBaths(dto.getBaths());
        listing.setCapacity(dto.getCapacity());
        listing.setSquareFeet(dto.getSquareFeet());
        listing.setAvailable(dto.isAvailable());
        listing.setCheckInTime(dto.getCheckInTime());
        listing.setCheckOutTime(dto.getCheckOutTime());

        listing.setHost(host);

        Amenities amenities = new Amenities();
        amenities.setListing(listing);

        if (dto.getAmenities() != null) {
            applyAmenities(amenities, dto.getAmenities());
        }

        listing.setAmenities(amenities);

        // ---- Images ----
        if (dto.getImageUrls() != null) {
            dto.getImageUrls().forEach(url -> {
                ListingImage img = new ListingImage();
                img.setImageUrl(url);
                img.setListing(listing);
                listing.getImages().add(img);
            });
        }

        Listing saved = listingRepo.save(listing);

        return toDetail(saved);
    }


    public List<ListingSummary> getAllListings() {
        return listingRepo.findAll()
                .stream()
                .limit(30)
                .map(this::toSummary)
                .toList();
    }

    public List<ListingSummary> getAllOwnedListings() {
        Long userId = SecurityUtils.getCurrentUserId();

        return listingRepo.findByHostId(userId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public ListingDetail getListingDetail(Long id) {
        return toDetail(getListingEntity(id));
    }

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

        if (dto.getCheckInTime() != null)
            listing.setCheckInTime(dto.getCheckInTime());

        if (dto.getCheckOutTime() != null)
            listing.setCheckOutTime(dto.getCheckOutTime());

        if (dto.getAmenities() != null) {

            Amenities amenities = listing.getAmenities();

            if (amenities == null) {
                amenities = new Amenities();
                amenities.setListing(listing);
                listing.setAmenities(amenities);
            }

            applyAmenities(amenities, dto.getAmenities());
        }

        Listing saved = listingRepo.save(listing);

        return toDetail(saved);
    }


    public void removeListing(Long id) {
        Listing listing = getListingEntity(id);
        assertOwnerOrAdmin(listing);
        listingRepo.delete(listing);
    }


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

    private void applyAmenities(Amenities amenities, List<String> reqAmenities) {

        amenities.setWifi(reqAmenities.contains("wifi"));
        amenities.setTv(reqAmenities.contains("tv"));
        amenities.setKitchen(reqAmenities.contains("kitchen"));
        amenities.setWasher(reqAmenities.contains("washer"));
        amenities.setDryer(reqAmenities.contains("dryer"));
        amenities.setPetsAllowed(reqAmenities.contains("petsAllowed"));
        amenities.setSmokeAlarm(reqAmenities.contains("smokeAlarm"));
        amenities.setDesertView(reqAmenities.contains("desertView"));
        amenities.setMountainView(reqAmenities.contains("mountainView"));
        amenities.setValleyView(reqAmenities.contains("valleyView"));
    }

    public ListingSummary toSummary(Listing listing) {

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

        if (!listing.getImages().isEmpty()) {
            s.setImageUrl(
                    listing.getImages().getFirst().getImageUrl()
            );
        }

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
        d.setCheckInTime(listing.getCheckInTime());
        d.setCheckOutTime(listing.getCheckOutTime());

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
                        .map(ListingImage::getImageUrl)
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

        // ---- Amenities ----
        Amenities a = listing.getAmenities();
        if (a != null) {
            List<String> amenities = new ArrayList<>();

            if (a.isWifi()) amenities.add("wifi");
            if (a.isTv()) amenities.add("tv");
            if (a.isKitchen()) amenities.add("kitchen");
            if (a.isWasher()) amenities.add("washer");
            if (a.isDryer()) amenities.add("dryer");
            if (a.isPetsAllowed()) amenities.add("petsAllowed");
            if (a.isSmokeAlarm()) amenities.add("smokeAlarm");
            if (a.isDesertView()) amenities.add("desertView");
            if (a.isMountainView()) amenities.add("mountainView");
            if (a.isValleyView()) amenities.add("valleyView");

            d.setAmenities(amenities);
        }

        return d;
    }
}