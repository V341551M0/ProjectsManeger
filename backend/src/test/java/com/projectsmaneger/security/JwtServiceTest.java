package com.projectsmaneger.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    @Test
    void shouldGenerateValidToken() {

        String secret = "ajbsajBAKSnkjsBJABSjkcbaBKJASBKJABSKKJKJ";

        JwtService jwtService = new JwtService(secret);

        String token = jwtService.generateToken("verissimo");

        assertNotNull(token);
        assertFalse(token.isBlank());

        String[] parts = token.split("\\.");

        assertEquals(3, parts.length);
    }
}
