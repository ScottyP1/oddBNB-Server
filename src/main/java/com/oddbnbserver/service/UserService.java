package com.oddbnbserver.service;


import com.oddbnbserver.models.dto.user.UserCreateRequest;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.models.dto.user.UserUpdateRequest;
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
    public UserResponse createNewUser(UserCreateRequest dto) {

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(dto.getPassword());

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
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserUpdateRequest dto) {
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
            existing.setPassword(dto.getPassword());
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
        User user = getUser(id);
        userRepo.delete(user);
    }
}
