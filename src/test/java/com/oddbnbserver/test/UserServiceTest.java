package com.oddbnbserver.test;

import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.user.UserResponse;
import com.oddbnbserver.models.dto.user.UserUpdateRequest;
import com.oddbnbserver.repositories.BookingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BookingRepo bookingRepo;

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

        user.setReviewsWritten(new ArrayList<>());
        user.setFavorites(new ArrayList<>());
        user.setHostedListings(new ArrayList<>());

        var auth = new UsernamePasswordAuthenticationToken(
                1L,
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
    void shouldThrowWhenUpdatingDifferentUser() {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("hack@test.com");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(2L, request)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
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
    void shouldThrowWhenUpdatingUserNotFound() {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("test@test.com");

        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(1L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void shouldThrowWhenAccessingDifferentUser() {

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUser(2L)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void shouldUpdateUserEmail() {

        UserUpdateRequest updatedUser = new UserUpdateRequest();
        updatedUser.setEmail("newEmail@gmail.com");

        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepo.save(user))
                .thenReturn(user);

        userService.updateUser(1L, updatedUser);

        assertEquals("newEmail@gmail.com", user.getEmail());
    }

    @Test
    void shouldReturnAllUsers() {

        User user = new User();
        user.setId(1L);
        user.setFirstName("test");


        when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserResponse> users = userService.getUsers();

        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals("test", users.get(0).getFirstName());
    }

    @Test
    void shouldRemoveUserAccount() {
        Long userId = SecurityUtils.getCurrentUserId();

        User user = new User();
        user.setId(1l);

        when(userRepo.findById(1l)).thenReturn(Optional.of(user));

        userService.removeUser(1l);

        verify(userRepo).delete(user);

    }

    @Test
    void shouldUpgradeGuestToHost() {

        user.setRole(User.Role.GUEST);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        UserResponse response = userService.becomeHost();

        assertEquals(User.Role.HOST, response.getRole());
        verify(userRepo).save(user);
    }

}
