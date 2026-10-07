package com.projectsmaneger.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;
import com.projectsmaneger.user.UserResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class AuthServiceTest {

  private AuthenticationManager authenticationManager;
  private Authentication authentication;
  private JwtService jwtService;
  private UserRepository userRepository;
  private AuthService authService;

  @BeforeEach
  void setUp() {

    authenticationManager = mock(AuthenticationManager.class);

    authentication = mock(Authentication.class);

    jwtService = mock(JwtService.class);

    userRepository = mock(UserRepository.class);

    authService = new AuthService(authenticationManager, jwtService, userRepository);
  }

  @Test
  void shouldAuthenticateUserAndGenerateToken() {

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    when(authentication.getName()).thenReturn("verissimo");

    when(jwtService.generateToken("verissimo")).thenReturn("jwt-token");

    String result = authService.authenticate("verissimo", "senha");

    assertEquals("jwt-token", result);

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

    verify(jwtService).generateToken("verissimo");
  }

  @Test
  void shouldFindAuthenticatedUser() {

    User user = new User("verissimo", "verissimo@test.local", "password-hash");

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(user));

    UserResponse result = authService.findAuthenticatedUser("verissimo");

    assertNotNull(result);

    assertEquals("verissimo", result.username());

    assertEquals("verissimo@test.local", result.email());

    assertEquals(true, result.enabled());

    verify(userRepository).findByUsername("verissimo");
  }
}
