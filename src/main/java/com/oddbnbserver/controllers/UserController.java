package com.oddbnbserver.controllers;

import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.user.UserCreateRequest;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.models.dto.user.UserUpdateRequest;
import com.oddbnbserver.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE (POST)
    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.createNewUser(request);
    }

    // READ (GET)
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // UPDATE (PATCH)
    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    // DELETE (DELETE)
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
    }
}