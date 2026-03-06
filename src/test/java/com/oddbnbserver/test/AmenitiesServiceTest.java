package com.oddbnbserver.test;

import com.oddbnbserver.models.Amenities;
import com.oddbnbserver.models.Listing;
import com.oddbnbserver.repositories.AmenitiesRepo;
import com.oddbnbserver.service.AmenitiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmenitiesServiceTest {

    @Mock
    private AmenitiesRepo amenitiesRepo;

    @InjectMocks
    private AmenitiesService amenitiesService;

    private Amenities amenities;
    private Listing listing;

    @BeforeEach
    void setup() {
        listing = new Listing();
        listing.setId(1L);

        amenities = new Amenities();
        amenities.setId(1L);
        amenities.setListing(listing);
        amenities.setTv(true);
    }

    @Test
    void shouldCreateAmenities() {

        when(amenitiesRepo.save(amenities)).thenReturn(amenities);

        Amenities result = amenitiesService.create(amenities);

        assertEquals(1L, result.getId());

        verify(amenitiesRepo, times(1)).save(amenities);
    }


    @Test
    void shouldReturnAmenitiesForListing() {

        when(amenitiesRepo.findById(1L)).thenReturn(Optional.of(amenities));

        Amenities result = amenitiesService.getAmenities(1L);

        assertEquals(true, result.isTv());
    }

    @Test
    void shouldDeleteAmenities() {

        when(amenitiesRepo.findById(1L)).thenReturn(Optional.of(amenities));

        amenitiesService.removeAmenities(1L);

        verify(amenitiesRepo, times(1)).delete(amenities);
    }

    @Test
    void shouldUpdateAmenities() {

        Amenities update = new Amenities();
        update.setTv(false);

        when(amenitiesRepo.findById(1L)).thenReturn(Optional.of(amenities));
        when(amenitiesRepo.save(any(Amenities.class))).thenReturn(amenities);

        Amenities result = amenitiesService.updateAmenities(1L, update);

        assertEquals(false, result.isTv());

        verify(amenitiesRepo).save(amenities);
    }
}