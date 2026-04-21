package com.oddbnbserver.service;

import com.oddbnbserver.models.*;
import com.oddbnbserver.models.dto.auth.RegisterRequest;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.models.dto.user.UserUpdateRequest;
import com.oddbnbserver.repositories.BookingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final BookingRepo bookingRepo;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, BookingRepo bookingRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.bookingRepo = bookingRepo;
    }

    // CREATE
    public UserResponse createNewUser(RegisterRequest dto) {

        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already in use"
            );
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.GUEST);

        User saved = userRepo.save(user);

        return toResponse(saved);
    }

    // READ (Current User Only)
    public UserResponse getUser(Long id) {
        User user = getUserEntity(id);
        return toResponse(user);
    }

    public List<UserResponse> getUsers() {
        return userRepo.findAll()
                .stream()
                .limit(50)
                .map(this::toResponse)
                .toList();
    }

    public UserResponse toUserResponse(User user) {
        return toResponse(user);
    }

    public UserResponse getCurrentUser() {

        Long userId = SecurityUtils.getRequiredUserId();

        return getUserResponse(userId);
    }

    public User getCurrentUserEntity() {
        Long userId = SecurityUtils.getRequiredUserId();

        return userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserUpdateRequest dto) {

        User existing = getUserEntity(id);

        if (dto.getRole() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role updates not allowed"
            );
        }

        if (dto.getEmail() != null
                && !existing.getEmail().equals(dto.getEmail())
                && userRepo.existsByEmail(dto.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already in use"
            );
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

        return getUserResponse(id);
    }

    public UserResponse becomeHost() {
        User existing = getCurrentUserEntity();

        if (existing.getRole() == User.Role.GUEST) {
            existing.setRole(User.Role.HOST);
            userRepo.save(existing);
        }

        return getUserResponse(existing.getId());
    }

    // DELETE
    public void removeUser(Long id) {
        User user = getUserEntity(id);
        userRepo.delete(user);
    }

    // USER WITH RELATIONSHIPS
    public UserResponse getUserResponse(Long id) {

        User user = getUserEntity(id);
        UserResponse res = toResponse(user);

        res.setReviewIds(
                user.getReviewsWritten()
                        .stream()
                        .map(Review::getId)
                        .toList()
        );

        res.setHostedListingIds(
                user.getHostedListings()
                        .stream()
                        .map(Listing::getId)
                        .toList()
        );

        res.setFavoriteIds(
                user.getFavorites()
                        .stream()
                        .map(Favorite::getId)
                        .toList()
        );

        res.setBookingIds(
                bookingRepo.findByGuest_Id(user.getId())
                        .stream()
                        .map(Booking::getId)
                        .toList()
        );
        return res;
    }

    // PRIVATE HELPERS
    private User getUserEntity(Long id) {

        Long userId = SecurityUtils.getRequiredUserId();

        if (!userId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another user's data"
            );
        }

        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private UserResponse toResponse(User user) {

        UserResponse res = new UserResponse();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFirstName(user.getFirstName());
        res.setLastName(user.getLastName());
        res.setRole(user.getRole());

        return res;
    }
}
