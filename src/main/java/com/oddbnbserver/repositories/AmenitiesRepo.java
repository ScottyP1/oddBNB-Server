package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenitiesRepo extends JpaRepository<Amenities, Long> {
    
}
