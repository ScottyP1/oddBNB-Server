package com.oddbnbserver.repositories;

import com.oddbnbserver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

}
