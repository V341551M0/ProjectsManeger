package com.projectsmaneger.github.controller;

import com.projectsmaneger.github.service.GitHubOAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github/oauth")
public class GitHubOAuthController {

  private static final String STATE_COOKIE = "github_oauth_state";

  private final GitHubOAuthService gitHubOAuthService;

  public GitHubOAuthController(GitHubOAuthService gitHubOAuthService) {
    this.gitHubOAuthService = gitHubOAuthService;
  }

  @GetMapping("/authorize")
  public ResponseEntity<Void> authorize(HttpServletResponse response) {
    String state = gitHubOAuthService.generateState();

    Cookie cookie = new Cookie(STATE_COOKIE, state);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/api/github/oauth");
    cookie.setMaxAge(300);

    response.addCookie(cookie);

    String authorizationUrl = gitHubOAuthService.buildAuthorizationUrl(state);

    return ResponseEntity.status(302).header(HttpHeaders.LOCATION, authorizationUrl).build();
  }
}
