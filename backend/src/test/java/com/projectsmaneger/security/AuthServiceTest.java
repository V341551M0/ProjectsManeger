package com.projectsmaneger.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class AuthServiceTest {

    @Test
    void shouldAuthenticateUserAndGenerateToken() {

        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);

        Authentication authentication =
                mock(Authentication.class);

        JwtService jwtService =
                mock(JwtService.class);

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("verissimo");

        when(jwtService.generateToken("verissimo"))
                .thenReturn("jwt-token");

        AuthService authService =
                new AuthService(
                        authenticationManager,
                        jwtService
                );

        String result =
                authService.authenticate(
                        "verissimo",
                        "senha"
                );

        assertEquals("jwt-token", result);

        verify(authenticationManager).authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ));

        verify(jwtService).generateToken("verissimo");
    }
}
