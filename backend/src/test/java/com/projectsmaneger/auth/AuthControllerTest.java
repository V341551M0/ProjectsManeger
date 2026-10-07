package com.projectsmaneger.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.projectsmaneger.security.AuthService;
import com.projectsmaneger.security.JwtService;
import com.projectsmaneger.security.UserDetailsServiceImpl;
import com.projectsmaneger.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = AuthController.class,
    properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration")
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthService authService;

  @MockBean private JwtService jwtService;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @Test
  void shouldLoginUser() throws Exception {

    when(authService.authenticate(any(String.class), any(String.class))).thenReturn("jwt-token");

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .with(user("verissimo"))
                .contentType("application/json")
                .content(
                    """
                                {
                                    "username": "verissimo",
                                    "password": "senha"
                                }
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  @Test
  void shouldReturnAuthenticatedUser() throws Exception {

    UserResponse userResponse =
        new UserResponse(1L, "verissimo", "verissimo@test.local", true, null, null);

    when(authService.findAuthenticatedUser("verissimo")).thenReturn(userResponse);

    mockMvc
        .perform(get("/api/auth/me").with(user("verissimo")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("verissimo"))
        .andExpect(jsonPath("$.email").value("verissimo@test.local"))
        .andExpect(jsonPath("$.enabled").value(true));

    verify(authService).findAuthenticatedUser("verissimo");
  }

  @Test
  void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {

    when(authService.authenticate("verissimo", "senha"))
        .thenThrow(
            new org.springframework.security.authentication.BadCredentialsException(
                "Invalid credentials"));

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .with(user("verissimo"))
                .contentType("application/json")
                .content(
                    """
                    {
                        "username": "verissimo",
                        "password": "senha"
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message").value("Invalid username or password"));

    verify(authService).authenticate("verissimo", "senha");
  }
}
