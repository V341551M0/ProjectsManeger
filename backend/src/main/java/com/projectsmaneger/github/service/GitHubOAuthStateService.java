package com.projectsmaneger.github.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubOAuthStateService {

  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private static final int STATE_BYTES = 32;

  private final byte[] secret;
  private final SecureRandom secureRandom = new SecureRandom();

  public GitHubOAuthStateService(@Value("${jwt.secret}") String secret) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
  }

  public String generateState() {
    byte[] bytes = new byte[STATE_BYTES];
    secureRandom.nextBytes(bytes);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public String signUserBinding(String state, String username) {
    String payload = state + ":" + username;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(hmac(payload.getBytes(StandardCharsets.UTF_8)));
  }

  public boolean verifyUserBinding(String state, String username, String signature) {
    if (state == null || username == null || signature == null) {
      return false;
    }

    byte[] expected;

    try {
      expected = hmac((state + ":" + username).getBytes(StandardCharsets.UTF_8));
    } catch (IllegalStateException exception) {
      return false;
    }

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
}
