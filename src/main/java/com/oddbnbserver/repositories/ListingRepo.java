package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepo extends JpaRepository<Listing, Long> {
}
