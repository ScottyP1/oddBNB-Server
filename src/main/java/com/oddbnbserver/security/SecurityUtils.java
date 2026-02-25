package com.oddbnbserver.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityUtils {

    public static Long getCurrentUserId() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Not authenticated");
        }

        return (Long) auth.getPrincipal();
    }

    public static boolean isAdmin() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        return auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
    }
}