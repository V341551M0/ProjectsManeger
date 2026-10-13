package com.projectsmaneger.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookEventController {

  private final WebhookEventService webhookEventService;
  private final GitHubWebhookSignatureVerifier signatureVerifier;
  private final ObjectMapper objectMapper;

  public WebhookEventController(
      WebhookEventService webhookEventService,
      GitHubWebhookSignatureVerifier signatureVerifier,
      ObjectMapper objectMapper) {
    this.webhookEventService = webhookEventService;
    this.signatureVerifier = signatureVerifier;
    this.objectMapper = objectMapper;
  }

  @PostMapping("/github")
  public ResponseEntity<Void> receiveGithubWebhook(
      @RequestHeader("X-GitHub-Delivery") String githubDeliveryId,
      @RequestHeader("X-GitHub-Event") String eventType,
      @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
      @RequestBody byte[] rawPayload)
      throws IOException {
    if (!signatureVerifier.isValid(rawPayload, signature)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    JsonNode payload = objectMapper.readTree(rawPayload);
    JsonNode repositoryNode = payload.path("repository");
    if (!repositoryNode.path("id").canConvertToLong()
        || repositoryNode.path("id").asLong() <= 0
        || githubDeliveryId.isBlank()
        || eventType.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    long githubRepositoryId = repositoryNode.path("id").asLong();

    String action = payload.path("action").isMissingNode() ? null : payload.path("action").asText();

    webhookEventService.registerEvent(
        githubRepositoryId, githubDeliveryId, eventType, action, payload);

    return ResponseEntity.ok().build();
  }
}
