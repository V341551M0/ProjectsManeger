package com.projectsmaneger.webhook;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class GitHubWebhookSignatureVerifierTest {

  private static final String SECRET = "test-only-webhook-secret";

  @Test
  void acceptsOnlyMatchingSha256Signature() throws Exception {
    byte[] payload = "{\"action\":\"opened\"}".getBytes(StandardCharsets.UTF_8);
    GitHubWebhookSignatureVerifier verifier = new GitHubWebhookSignatureVerifier(SECRET);

    assertThat(verifier.isValid(payload, signature(payload))).isTrue();
    assertThat(verifier.isValid(payload, "sha256=incorrect")).isFalse();
    assertThat(verifier.isValid(payload, null)).isFalse();
  }

  @Test
  void rejectsWhenSecretIsNotConfigured() throws Exception {
    byte[] payload = "{}".getBytes(StandardCharsets.UTF_8);
    GitHubWebhookSignatureVerifier verifier = new GitHubWebhookSignatureVerifier("");

    assertThat(verifier.isValid(payload, signature(payload))).isFalse();
  }

  private String signature(byte[] payload) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    StringBuilder hex = new StringBuilder();
    for (byte value : mac.doFinal(payload)) {
      hex.append(String.format("%02x", value & 0xff));
    }
    return "sha256=" + hex;
  }
}
