package com.projectsmaneger.github;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.projectsmaneger.github.client.GitHubOAuthClient;
import com.projectsmaneger.github.controller.GitHubOAuthController;
import com.projectsmaneger.github.dto.GitHubUserResponse;
import com.projectsmaneger.github.service.GitHubAccountService;
import com.projectsmaneger.github.service.GitHubOAuthService;
import com.projectsmaneger.github.service.GitHubOAuthStateService;
import com.projectsmaneger.security.JwtService;
import com.projectsmaneger.security.UserDetailsServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = GitHubOAuthController.class,
    properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration")
class GitHubOAuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private GitHubOAuthService oauthService;

  @MockBean private GitHubOAuthStateService stateService;

  @MockBean private GitHubOAuthClient oauthClient;

  @MockBean private GitHubAccountService accountService;

  @MockBean private JwtService jwtService;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @Test
  void callbackLinksAuthenticatedGitHubIdentityToStateOwner() throws Exception {
    when(stateService.consumeState("valid-state")).thenReturn(Optional.of("local-user"));
    when(oauthClient.exchangeCodeForAccessToken("oauth-code")).thenReturn("access-token");
    when(oauthClient.getAuthenticatedUser("access-token"))
        .thenReturn(new GitHubUserResponse(123L, "octocat", "Octocat", null));

    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "valid-state")
                .param("code", "oauth-code")
                .cookie(new jakarta.servlet.http.Cookie("github_oauth_state", "valid-state")))
        .andExpect(status().isFound())
        .andExpect(
            header().string(HttpHeaders.LOCATION, "http://localhost:5173/?github=connected"));

    verify(accountService)
        .linkAccount(
            "local-user", new GitHubUserResponse(123L, "octocat", "Octocat", null), "access-token");
  }

  @Test
  void callbackRejectsMissingOrMismatchedState() throws Exception {
    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("code", "oauth-code"))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "request-state")
                .param("code", "oauth-code")
                .cookie(new jakarta.servlet.http.Cookie("github_oauth_state", "cookie-state")))
        .andExpect(status().isBadRequest());

    verify(stateService, never()).consumeState(anyString());
    verify(oauthClient, never()).exchangeCodeForAccessToken(anyString());
  }

  @Test
  void callbackRejectsExpiredOrPreviouslyConsumedState() throws Exception {
    when(stateService.consumeState("expired-state")).thenReturn(Optional.empty());

    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "expired-state")
                .param("code", "oauth-code")
                .cookie(new jakarta.servlet.http.Cookie("github_oauth_state", "expired-state")))
        .andExpect(status().isBadRequest());

    verify(oauthClient, never()).exchangeCodeForAccessToken(anyString());
  }

  @Test
  void callbackHandlesCancellationAndMissingCode() throws Exception {
    when(stateService.consumeState("cancelled-state")).thenReturn(Optional.of("local-user"));

    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "cancelled-state")
                .param("error", "access_denied")
                .cookie(new jakarta.servlet.http.Cookie("github_oauth_state", "cancelled-state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:5173/?github=error"));

    verify(oauthClient, never()).exchangeCodeForAccessToken(anyString());

    when(stateService.consumeState("missing-code-state")).thenReturn(Optional.of("local-user"));
    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "missing-code-state")
                .cookie(
                    new jakarta.servlet.http.Cookie("github_oauth_state", "missing-code-state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:5173/?github=error"));

    verify(oauthClient, never()).exchangeCodeForAccessToken(anyString());
  }

  @Test
  void callbackReportsExternalApiFailure() throws Exception {
    when(stateService.consumeState("failed-state")).thenReturn(Optional.of("local-user"));
    when(oauthClient.exchangeCodeForAccessToken("oauth-code"))
        .thenThrow(new IllegalStateException("external API failed"));

    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "failed-state")
                .param("code", "oauth-code")
                .cookie(new jakarta.servlet.http.Cookie("github_oauth_state", "failed-state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:5173/?github=error"));
  }

  @Test
  void callbackReportsFailureToLinkAnAlreadyLinkedAccount() throws Exception {
    when(stateService.consumeState("link-failure-state")).thenReturn(Optional.of("local-user"));
    when(oauthClient.exchangeCodeForAccessToken("oauth-code")).thenReturn("access-token");
    GitHubUserResponse identity = new GitHubUserResponse(123L, "octocat", "Octocat", null);
    when(oauthClient.getAuthenticatedUser("access-token")).thenReturn(identity);
    when(accountService.linkAccount("local-user", identity, "access-token"))
        .thenThrow(new IllegalStateException("GitHub account is linked to another user"));

    mockMvc
        .perform(
            get("/api/github/oauth/callback")
                .with(user("oauth-callback"))
                .param("state", "link-failure-state")
                .param("code", "oauth-code")
                .cookie(
                    new jakarta.servlet.http.Cookie("github_oauth_state", "link-failure-state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:5173/?github=error"));
  }
}
