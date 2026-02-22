package com.oddbnbserver.controllers;

import com.oddbnbserver.models.Amenities;
import com.oddbnbserver.service.AmenitiesService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/amenities")
public class AmenitiesController {

    private final AmenitiesService amenitiesService;

    public AmenitiesController(AmenitiesService amenitiesService) {
        this.amenitiesService = amenitiesService;
    }

    // CREATE
    @PostMapping
    public Amenities create(@RequestBody Amenities amenities) {
        return amenitiesService.create(amenities);
    }

    // READ
    @GetMapping("/{id}")
    public Amenities get(@PathVariable Long id) {
        return amenitiesService.getAmenities(id);
    }

    // UPDATE (PATCH = partial update)
    @PatchMapping("/{id}")
    public Amenities update(@PathVariable Long id,
                            @RequestBody Amenities amenities) {
        return amenitiesService.updateAmenities(id, amenities);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        amenitiesService.removeAmenities(id);
    }
}
