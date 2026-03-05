package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepo extends JpaRepository<Listing, Long> {

    List<Listing> findByHostId(Long host_id);
}
