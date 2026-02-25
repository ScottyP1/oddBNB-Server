package com.oddbnbserver.test;

import com.oddbnbserver.models.User;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        user.setPassword_hash("test");
        user.setRole(User.Role.ADMIN);
    }

    @Test
    void shouldReturnUserWhenFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals("Cody", result.getFirstName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepo.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.getUser(1L)
        );

        assertEquals("User not found", ex.getMessage());
    }

}
