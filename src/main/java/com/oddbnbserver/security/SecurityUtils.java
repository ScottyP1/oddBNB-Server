package com.oddbnbserver.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public class SecurityUtils {

    public static Long getCurrentUserId() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Long id) {
            return id;
        }

        if (principal instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    public static Long getRequiredUserId() {

        Long id = getCurrentUserId();

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return id;
    }

    public static boolean isAdmin() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
    }
}