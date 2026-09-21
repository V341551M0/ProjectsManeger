package com.projectsmaneger.webhook;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.projectsmaneger.repository.Repository;
import com.projectsmaneger.repository.RepositoryRepository;

@Service
public class WebhookEventService {

    private final WebhookEventRepository webhookEventRepository;
    private final RepositoryRepository repositoryRepository;

    public WebhookEventService(
            WebhookEventRepository webhookEventRepository,
            RepositoryRepository repositoryRepository
    ) {
        this.webhookEventRepository = webhookEventRepository;
        this.repositoryRepository = repositoryRepository;
    }

    public WebhookEvent registerEvent(
            Long githubRepositoryId,
            String githubDeliveryId,
            String eventType,
            String action,
            JsonNode payload
    ) {
        Repository repository = repositoryRepository
                .findByGithubRepositoryId(githubRepositoryId)
                .orElse(null);

        WebhookEvent webhookEvent = new WebhookEvent(
                repository,
                githubDeliveryId,
                eventType,
                action,
                payload,
                WebhookEvent.ProcessingStatus.RECEIVED,
                null
        );

        return webhookEventRepository.save(webhookEvent);
    }
}
