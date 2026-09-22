package com.projectsmaneger.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordGeneratorTest {

    @Test
    void generatePasswordHash() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String hash = encoder.encode("senha");

        System.out.println("BCrypt: " + hash);
    }
}
