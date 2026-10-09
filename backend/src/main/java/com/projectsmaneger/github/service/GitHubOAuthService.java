package com.projectsmaneger.github.service;

import com.projectsmaneger.github.config.GitHubOAuthProperties;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GitHubOAuthService {

  private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";

  private final GitHubOAuthProperties properties;
  private final SecureRandom secureRandom = new SecureRandom();

  public GitHubOAuthService(GitHubOAuthProperties properties) {
    this.properties = properties;
  }

  public String generateState() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
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
