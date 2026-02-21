package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review, Long> {
}
