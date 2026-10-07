package com.projectsmaneger.github.controller;

import com.projectsmaneger.github.service.GitHubOAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github/oauth")
public class GitHubOAuthController {

  private final GitHubOAuthService gitHubOAuthService;

  public GitHubOAuthController(GitHubOAuthService gitHubOAuthService) {
    this.gitHubOAuthService = gitHubOAuthService;
  }

  @GetMapping("/authorize")
  public ResponseEntity<Void> authorize(@RequestParam String state) {
    String authorizationUrl = gitHubOAuthService.buildAuthorizationUrl(state);

    return ResponseEntity.status(302).header(HttpHeaders.LOCATION, authorizationUrl).build();
  }
}
