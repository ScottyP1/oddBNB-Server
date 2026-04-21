package com.oddbnbserver.controllers;

import com.oddbnbserver.models.dto.auth.AuthResponse;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.models.dto.user.UserUpdateRequest;
import com.oddbnbserver.security.JwtService;
import com.oddbnbserver.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // READ (GET)
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUserResponse(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }


    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping("/me/become-host")
    public AuthResponse becomeHost() {
        UserResponse user = userService.becomeHost();
        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, user);
    }

    // PATCH
    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
    }
}
