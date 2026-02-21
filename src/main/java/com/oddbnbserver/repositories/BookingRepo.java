package com.oddbnbserver.repositories;

import com.oddbnbserver.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, Long> {
}
