package com.oddbnbserver.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "very-long-secret-key-very-long-secret-key";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Create token with user ID
    public String generateToken(Long userId, String role) {

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)        // ⭐ NEW
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 86400000))
                .signWith(getKey())
                .compact();
    }

    // Extract user ID
    public Long extractUserId(String token) {

        String id = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.parseLong(id);
    }

    public String extractRole(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}
