package com.oddbnbserver.test;

import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.service.UserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setFirstName("Cody");
        user.setLastName("Scott");
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setRole(User.Role.ADMIN);

        var auth = new UsernamePasswordAuthenticationToken(
                1L,  // principal = user ID
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnUserWhenFound() {

        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.getUser(1L);

        assertEquals(1L, result.getId());
        assertEquals("Cody", result.getFirstName());
        assertEquals("Scott", result.getLastName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(User.Role.ADMIN, result.getRole());
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepo.findById(1L))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUser(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("User not found", ex.getReason());
    }

    @Test
    void shouldThrowWhenAccessingDifferentUser() {

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUser(2L)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}