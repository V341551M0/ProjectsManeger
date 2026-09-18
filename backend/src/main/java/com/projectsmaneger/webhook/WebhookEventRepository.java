package com.projectsmaneger.webhook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository
    extends JpaRepository<WebhookEvent, Long> {
        List<WebhookEvent> findByRepositoryId(Long repositoryId);

        Optional<WebhookEvent> findByGithubDeliveryId(String githubDeliveryId);
}
