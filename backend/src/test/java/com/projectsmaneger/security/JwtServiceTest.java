package com.projectsmaneger.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    @Test
    void shouldGenerateValidToken() {

        String secret =
                "ajbsajBAKSnkjsBJABSjkcbaBKJASBKJABSKKJKJ";

        JwtService jwtService =
                new JwtService(secret);

        String token =
                jwtService.generateToken("verissimo");

        assertNotNull(token);
        assertFalse(token.isBlank());

        String[] parts =
                token.split("\\.");

        assertEquals(3, parts.length);
    }

    @Test
    void shouldExtractUsernameFromToken() {

        String secret =
                "ajbsajBAKSnkjsBJABSjkcbaBKJASBKJABSKKJKJ";

        JwtService jwtService =
                new JwtService(secret);

        String token =
                jwtService.generateToken("verissimo");

        String username =
                jwtService.extractUsername(token);

        assertEquals("verissimo", username);
    }

    @Test
    void shouldValidateToken() {

        String secret =
                "ajbsajBAKSnkjsBJABSjkcbaBKJASBKJABSKKJKJ";

        JwtService jwtService =
                new JwtService(secret);

        String token =
                jwtService.generateToken("verissimo");

        UserDetails userDetails =
                User.withUsername("verissimo")
                        .password("senha")
                        .roles("USER")
                        .build();

        assertTrue(
                jwtService.isTokenValid(
                        token,
                        userDetails
                )
        );
    }
}
