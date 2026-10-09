package com.projectsmaneger.github.controller;

import com.projectsmaneger.github.client.GitHubOAuthClient;
import com.projectsmaneger.github.dto.GitHubUserResponse;
import com.projectsmaneger.github.service.GitHubAccountService;
import com.projectsmaneger.github.service.GitHubOAuthService;
import com.projectsmaneger.github.service.GitHubOAuthStateService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github/oauth")
public class GitHubOAuthController {

  private static final String STATE_COOKIE = "github_oauth_state";
  private static final String FRONTEND_URL = "http://localhost:5173/";

  private final GitHubOAuthService gitHubOAuthService;
  private final GitHubOAuthStateService stateService;
  private final GitHubOAuthClient gitHubOAuthClient;
  private final GitHubAccountService gitHubAccountService;

  public GitHubOAuthController(
      GitHubOAuthService gitHubOAuthService,
      GitHubOAuthStateService stateService,
      GitHubOAuthClient gitHubOAuthClient,
      GitHubAccountService gitHubAccountService) {
    this.gitHubOAuthService = gitHubOAuthService;
    this.stateService = stateService;
    this.gitHubOAuthClient = gitHubOAuthClient;
    this.gitHubAccountService = gitHubAccountService;
  }

  @GetMapping("/authorize")
  public ResponseEntity<Void> authorize(
      org.springframework.security.core.Authentication authentication,
      HttpServletResponse response) {
    String username = authentication.getName();
    String state = stateService.createState(username);

    Cookie cookie = new Cookie(STATE_COOKIE, state);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/api/github/oauth");
    cookie.setMaxAge(300);
    response.addCookie(cookie);

    String authorizationUrl = gitHubOAuthService.buildAuthorizationUrl(state);

    return ResponseEntity.status(302).header(HttpHeaders.LOCATION, authorizationUrl).build();
  }

  @GetMapping("/callback")
  public ResponseEntity<Void> callback(
      @RequestParam(required = false) String code,
      @RequestParam(required = false) String state,
      @RequestParam(required = false) String error,
      @CookieValue(name = STATE_COOKIE, required = false) String stateCookie,
      HttpServletResponse response) {
    clearStateCookie(response);

    if (state == null || stateCookie == null || !state.equals(stateCookie)) {
      return ResponseEntity.badRequest().build();
    }

    var username = stateService.consumeState(state);

    if (username.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    if (error != null || code == null || code.isBlank()) {
      return redirectToFrontend("error");
    }

    try {
      String accessToken = gitHubOAuthClient.exchangeCodeForAccessToken(code);

      GitHubUserResponse githubUser = gitHubOAuthClient.getAuthenticatedUser(accessToken);

      gitHubAccountService.linkAccount(username.get(), githubUser, accessToken);

      return redirectToFrontend("connected");
    } catch (RuntimeException exception) {
      return redirectToFrontend("error");
    }
  }

  private void clearStateCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(STATE_COOKIE, "");
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/api/github/oauth");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  private ResponseEntity<Void> redirectToFrontend(String status) {
    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, FRONTEND_URL + "?github=" + status)
        .build();
  }
}
