package com.oddbnbserver.test;


import com.oddbnbserver.models.User;
import com.oddbnbserver.models.dto.listing.ListingSummary;
import com.oddbnbserver.repositories.ListingRepo;
import com.oddbnbserver.repositories.UserRepo;
import com.oddbnbserver.security.SecurityUtils;
import com.oddbnbserver.service.ListingService;
import com.oddbnbserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ListingServiceTest {

    @Mock
    private ListingRepo listingRepo;
    @Mock
    private UserRepo userRepo;


    @InjectMocks
    private ListingService listingService;

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
                1L,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void getOwnedListings(){
        Long userId = SecurityUtils.getRequiredUserId();

        <List> listings = listingRepo.findByHostId(userId)
                    .stream()
                .limit(5)
                    .toList();

        assertEquals(1L, result.getId());
        assertEquals("Cody", result.getFirstName());
        assertEquals("Scott", result.getLastName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(User.Role.ADMIN, result.getRole());
        }

    }
}
