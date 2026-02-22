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
    public Amenities updateAmenities(Amenities amenities) {
        Amenities existingAmenities = getAmenities(amenities.getId());

        existingAmenities.setDesert_view(amenities.isDesert_view());
        existingAmenities.setMountain_view(amenities.isMountain_view());
        existingAmenities.setValley_view(amenities.isValley_view());
        existingAmenities.setWifi(amenities.isWifi());
        existingAmenities.setTv(amenities.isTv());
        existingAmenities.setKitchen(amenities.isKitchen());
        existingAmenities.setWasher(amenities.isWasher());
        existingAmenities.setDryer(amenities.isDryer());
        existingAmenities.setPets_allowed(amenities.isPets_allowed());
        existingAmenities.setSmoke_alarm(amenities.isSmoke_alarm());

        return amenitiesRepo.save(existingAmenities);
    }

    // DELETE
    public void removeAmenities(Long id) {
        Amenities amenities = getAmenities(id);
        amenitiesRepo.delete(amenities);
    }

}
