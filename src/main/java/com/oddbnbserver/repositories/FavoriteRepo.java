package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepo extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndListingId(Long userId, Long listingId);
}
