package com.projectsmaneger.github.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubOAuthStateService {

  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private static final int STATE_BYTES = 32;
  private static final long STATE_TTL_SECONDS = 300;

  private final byte[] secret;
  private final SecureRandom secureRandom = new SecureRandom();

  private final ConcurrentHashMap<String, StateEntry> states = new ConcurrentHashMap<>();

  public GitHubOAuthStateService(@Value("${jwt.secret}") String secret) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
  }

  public String createState(String username) {
    removeExpiredStates();

    byte[] bytes = new byte[STATE_BYTES];
    secureRandom.nextBytes(bytes);

    String state = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    String signature = signUserBinding(state, username);

    states.put(
        state, new StateEntry(username, signature, Instant.now().plusSeconds(STATE_TTL_SECONDS)));

    return state;
  }

  public java.util.Optional<String> consumeState(String state) {
    if (state == null) {
        return java.util.Optional.empty();
    }

    StateEntry entry = states.remove(state);

    if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
        return java.util.Optional.empty();
    }

    return java.util.Optional.of(entry.username());
}

  private String signUserBinding(String state, String username) {
    byte[] signature = hmac((state + ":" + username).getBytes(StandardCharsets.UTF_8));

    return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
  }

  public String getSignature(String state, String username) {
    return signUserBinding(state, username);
  }

  private boolean verifyUserBinding(String state, String username, String signature) {
    byte[] expected = hmac((state + ":" + username).getBytes(StandardCharsets.UTF_8));

    byte[] provided;

    try {
      provided = Base64.getUrlDecoder().decode(signature);
    } catch (IllegalArgumentException exception) {
      return false;
    }

    return MessageDigest.isEqual(expected, provided);
  }

  private byte[] hmac(byte[] payload) {
    try {
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
      return mac.doFinal(payload);
    } catch (Exception exception) {
      throw new IllegalStateException("Could not sign OAuth state", exception);
    }
  }

  private void removeExpiredStates() {
    Instant now = Instant.now();

    states.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt()));
  }

  private record StateEntry(String username, String signature, Instant expiresAt) {}
}
