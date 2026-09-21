package com.projectsmaneger.webhook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookEventController {

    private final WebhookEventService webhookEventService;

    public WebhookEventController(WebhookEventService webhookEventService) {
        this.webhookEventService = webhookEventService;
    }

    @PostMapping("/github")
    public ResponseEntity<Void> receiveGithubWebhook(
            @RequestHeader("X-GitHub-Delivery") String githubDeliveryId,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody JsonNode payload
    ) {
        JsonNode repositoryNode = payload.path("repository");
        long githubRepositoryId = repositoryNode.path("id").asLong();

        String action = payload.path("action").isMissingNode()
                ? null
                : payload.path("action").asText();

        webhookEventService.registerEvent(
                githubRepositoryId,
                githubDeliveryId,
                eventType,
                action,
                payload
        );

        return ResponseEntity.ok().build();
    }
}