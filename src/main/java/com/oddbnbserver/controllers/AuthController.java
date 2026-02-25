package com.oddbnbserver.controllers;

import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.auth.LoginRequest;
import com.oddbnbserver.models.dto.auth.RegisterRequest;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.JwtService;
import com.oddbnbserver.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepo userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepo userRepo,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService, UserService userService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        return userService.createNewUser(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(
                user.getId(),
                user.getRole().name()
        );
    }
}