package com.oddbnbserver.controllers;

import com.oddbnbserver.dto.user.UserCreateRequest;
import com.oddbnbserver.dto.user.UserResponse;
import com.oddbnbserver.dto.user.UserUpdateRequest;
import com.oddbnbserver.models.User;
import com.oddbnbserver.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.createNewUser(request);
    }

    // READ
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // UPDATE (PATCH = partial update)
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