package com.oddbnbserver.service;

import com.oddbnbserver.models.Amenities;
import com.oddbnbserver.repositories.AmenitiesRepo;
import org.springframework.stereotype.Service;

@Service
public class AmenitiesService {
    private final AmenitiesRepo amenitiesRepo;

    public AmenitiesService(AmenitiesRepo amenitiesRepo) {
        this.amenitiesRepo = amenitiesRepo;
    }


    // CREATE
    public Amenities create(Amenities newAmenities) {

        return amenitiesRepo.save(newAmenities);
    }

    // READ
    public Amenities getAmenities(Long id) {
        return amenitiesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Amenities not found"));
    }

    // UPDATE
    public Amenities updateAmenities(Long id, Amenities amenities) {
        Amenities existingAmenities = getAmenities(id);

        existingAmenities.setDesertView(amenities.isDesertView());
        existingAmenities.setMountainView(amenities.isMountainView());
        existingAmenities.setValleyView(amenities.isValleyView());
        existingAmenities.setWifi(amenities.isWifi());
        existingAmenities.setTv(amenities.isTv());
        existingAmenities.setKitchen(amenities.isKitchen());
        existingAmenities.setWasher(amenities.isWasher());
        existingAmenities.setDryer(amenities.isDryer());
        existingAmenities.setPetsAllowed(amenities.isPetsAllowed());
        existingAmenities.setSmokeAlarm(amenities.isSmokeAlarm());

        return amenitiesRepo.save(existingAmenities);
    }

    // DELETE
    public void removeAmenities(Long id) {
        Amenities amenities = getAmenities(id);
        amenitiesRepo.delete(amenities);
    }

}
