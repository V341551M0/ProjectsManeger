package com.projectsmaneger.github;

import static org.assertj.core.api.Assertions.assertThat;

import com.projectsmaneger.github.service.GitHubOAuthStateService;
import org.junit.jupiter.api.Test;

class GitHubOAuthStateServiceTest {

  private final GitHubOAuthStateService stateService = new GitHubOAuthStateService();

  @Test
  void stateIsRandomAndBoundToTheLocalUser() {
    String first = stateService.createState("alice");
    String second = stateService.createState("bob");

    assertThat(first).isNotEqualTo(second);
    assertThat(stateService.consumeState(first)).contains("alice");
    assertThat(stateService.consumeState(second)).contains("bob");
  }

  @Test
  void stateCanOnlyBeConsumedOnce() {
    String state = stateService.createState("alice");

    assertThat(stateService.consumeState(state)).contains("alice");
    assertThat(stateService.consumeState(state)).isEmpty();
    assertThat(stateService.consumeState("invalid-state")).isEmpty();
    assertThat(stateService.consumeState(null)).isEmpty();
  }
}
