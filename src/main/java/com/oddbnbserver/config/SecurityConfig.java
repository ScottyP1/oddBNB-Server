package com.oddbnbserver.config;

import com.oddbnbserver.security.JwtAuthFilter;
import com.oddbnbserver.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // APIs don't need CSRF
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()

                        // Listing chain
                        .requestMatchers(HttpMethod.GET, "/listings/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/listings/**").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/listings/**").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/listings/**").hasAnyRole("HOST", "ADMIN")

                        // Booking chain
                        .requestMatchers(HttpMethod.GET, "/bookings/**").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/bookings/**").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/bookings/**").hasAnyRole("HOST", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}