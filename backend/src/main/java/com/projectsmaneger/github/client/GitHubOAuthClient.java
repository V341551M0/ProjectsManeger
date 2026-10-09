package com.projectsmaneger.github.client;

import com.projectsmaneger.github.config.GitHubOAuthProperties;
import com.projectsmaneger.github.dto.GitHubUserResponse;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubOAuthClient {

  private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

  private final RestClient restClient;
  private final GitHubOAuthProperties properties;

  public GitHubOAuthClient(GitHubOAuthProperties properties) {
    this.properties = properties;
    this.restClient = RestClient.builder().build();
  }

  public String exchangeCodeForAccessToken(String code) {
    Map<String, Object> response =
        restClient
            .post()
            .uri(ACCESS_TOKEN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(
                Map.of(
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "code", code,
                    "redirect_uri", properties.redirectUri()))
            .retrieve()
            .body(Map.class);

    if (response == null || response.get("access_token") == null) {
      throw new IllegalStateException("GitHub did not return an access token");
    }

    return response.get("access_token").toString();
  }

  public GitHubUserResponse getAuthenticatedUser(String accessToken) {
    return restClient
        .get()
        .uri("https://api.github.com/user")
        .headers(
            headers -> {
              headers.setBearerAuth(accessToken);
              headers.set("X-GitHub-Api-Version", "2022-11-28");
              headers.setAccept(
                  java.util.List.of(org.springframework.http.MediaType.APPLICATION_JSON));
            })
        .retrieve()
        .body(GitHubUserResponse.class);
  }
}
