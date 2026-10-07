package com.projectsmaneger.github.service;

import com.projectsmaneger.github.config.GitHubOAuthProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GitHubOAuthService {

  private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";

  private final GitHubOAuthProperties properties;

  public GitHubOAuthService(GitHubOAuthProperties properties) {
    this.properties = properties;
  }

  public String buildAuthorizationUrl(String state) {
    return UriComponentsBuilder.fromUriString(AUTHORIZE_URL)
        .queryParam("client_id", properties.clientId())
        .queryParam("redirect_uri", properties.redirectUri())
        .queryParam("scope", "repo read:user user:email")
        .queryParam("state", state)
        .build()
        .toUriString();
  }
}
