package com.oddbnbserver.service;


import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // CREATE
    public User createNewUser(User newUser) {
        if (userRepo.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        return userRepo.save(newUser);
    }

    // READ
    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // UPDATE
    public User updateUser(Long id, User user) {
        User existing = getUser(id);

        if (!existing.getEmail().equals(user.getEmail())
                && userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (user.getEmail() != null) {
            existing.setEmail(user.getEmail());
        }
        if (user.getFirstName() != null) {
            existing.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existing.setLastName(user.getLastName());
        }
        if (user.getPassword() != null) {
            existing.setPassword(user.getPassword());
        }

        return userRepo.save(existing);
    }

    // DELETE
    public void removeUser(Long id) {
        User user = getUser(id);
        userRepo.delete(user);
    }
}
