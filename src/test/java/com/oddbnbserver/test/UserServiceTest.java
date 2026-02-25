package com.oddbnbserver.test;

import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Cody");
        user.setLastName("Scott");
        user.setEmail("test");
        user.setPasswordHash("test");
        user.setRole(User.Role.ADMIN);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnUserWhenFound() {

        var auth = new UsernamePasswordAuthenticationToken(
                1L,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var auth = new UsernamePasswordAuthenticationToken(
                1L,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepo.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.getUser(1L)
        );

        assertEquals("User not found", ex.getMessage());
    }

}
