package com.projectsmaneger.github.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GitHubOAuthStateService {

  private static final int STATE_BYTES = 32;
  private static final long STATE_TTL_SECONDS = 300;

  private final SecureRandom secureRandom = new SecureRandom();

  private final ConcurrentHashMap<String, StateEntry> states = new ConcurrentHashMap<>();

  public String createState(String username) {
    removeExpiredStates();

    byte[] bytes = new byte[STATE_BYTES];
    secureRandom.nextBytes(bytes);

    String state = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    states.put(state, new StateEntry(username, Instant.now().plusSeconds(STATE_TTL_SECONDS)));

    return state;
  }

  public Optional<String> consumeState(String state) {
    if (state == null) {
      return Optional.empty();
    }

    StateEntry entry = states.remove(state);

    if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
      return Optional.empty();
    }

    return Optional.of(entry.username());
  }

  private void removeExpiredStates() {
    Instant now = Instant.now();

    states.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt()));
  }

  private record StateEntry(String username, Instant expiresAt) {}
}
