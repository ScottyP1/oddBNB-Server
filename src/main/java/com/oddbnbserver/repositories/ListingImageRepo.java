package com.oddbnbserver.repositories;

import com.oddbnbserver.models.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingImageRepo extends JpaRepository<ListingImage, Long> {
    boolean existsByImageUrl(String imageUrl);
}
