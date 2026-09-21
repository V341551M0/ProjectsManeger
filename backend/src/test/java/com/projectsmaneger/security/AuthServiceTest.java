package com.projectsmaneger.security;

import static org.junit.jupiter.api.Assertions.assertSame;
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
    void shouldAuthenticateUser() {

        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);

        Authentication authentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        AuthService authService =
                new AuthService(authenticationManager);

        Authentication result =
                authService.authenticate(
                        "verissimo",
                        "senha"
                );

        assertSame(authentication, result);

        verify(authenticationManager).authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ));
    }
}
