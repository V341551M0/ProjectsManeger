package com.projectsmaneger.webhook;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitHubWebhookSignatureVerifier {

  private final String secret;

  public GitHubWebhookSignatureVerifier(@Value("${github.webhook.secret:}") String secret) {
    this.secret = secret;
  }

  public boolean isValid(byte[] payload, String signature) {
    if (secret.isBlank() || signature == null || !signature.startsWith("sha256=")) {
      return false;
    }

    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] expected =
          ("sha256=" + toHex(mac.doFinal(payload))).getBytes(StandardCharsets.US_ASCII);
      return MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.US_ASCII));
    } catch (java.security.GeneralSecurityException exception) {
      throw new IllegalStateException("Unable to verify GitHub webhook signature", exception);
    }
  }

  private String toHex(byte[] bytes) {
    StringBuilder hex = new StringBuilder(bytes.length * 2);
    for (byte value : bytes) {
      hex.append(Character.forDigit((value >>> 4) & 0xF, 16));
      hex.append(Character.forDigit(value & 0xF, 16));
    }
    return hex.toString();
  }
}
