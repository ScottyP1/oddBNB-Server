package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepo extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndListingId(Long userId, Long listingId);

    Optional<Favorite> findByUserIdAndListingId(Long userId, Long listingId);

    List<Favorite> findAllByUserId(Long userId);
}
