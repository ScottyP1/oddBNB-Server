package com.oddbnbserver.service;


import com.oddbnbserver.models.Review;
import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.auth.RegisterRequest;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.models.dto.user.UserUpdateRequest;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    public UserResponse createNewUser(RegisterRequest dto) {

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(
                passwordEncoder.encode(dto.getPassword())
        );
        User saved = userRepo.save(user);

        UserResponse response = new UserResponse();
        response.setId(saved.getId());
        response.setEmail(saved.getEmail());
        response.setFirstName(saved.getFirstName());
        response.setLastName(saved.getLastName());

        return response;
    }

    // READ
    public User getUser(Long id) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(id)) {
            throw new RuntimeException("Forbidden");
        }

        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserUpdateRequest dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(id)) {
            throw new RuntimeException("Forbidden");
        }

        User existing = getUser(id);

        if (dto.getEmail() != null
                && !existing.getEmail().equals(dto.getEmail())
                && userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            existing.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            existing.setLastName(dto.getLastName());
        }
        if (dto.getPassword() != null) {
            existing.setPasswordHash(
                    passwordEncoder.encode(dto.getPassword())
            );
        }

        User saved = userRepo.save(existing);

        UserResponse response = new UserResponse();
        response.setId(existing.getId());
        response.setEmail(existing.getEmail());
        response.setFirstName(existing.getFirstName());
        response.setLastName(existing.getLastName());

        return response;
    }

    // DELETE
    public void removeUser(Long id) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(id)) {
            throw new RuntimeException("Forbidden");
        }

        User user = getUser(id);
        userRepo.delete(user);
    }

    public UserResponse getUserResponse(Long id) {

        User user = getUser(id);
        Review review = getV

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setReviewsWritten(user.getReviewsWritten());

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setReviewsWritten(user.getReviewsWritten());

        return res;
    }
}
