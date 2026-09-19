package com.projectsmaneger.webhook;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsmaneger.github.GitHubAccount;
import com.projectsmaneger.github.GitHubAccountRepository;
import com.projectsmaneger.repository.Repository;
import com.projectsmaneger.repository.RepositoryRepository;
import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WebhookEventRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private WebhookEventRepository webhookEventRepository;

    @Test
    void shouldPersistAndFindWebhookEvents() throws Exception {
        String suffix = UUID.randomUUID()
            .toString()
            .substring(0, 8);

        User user = new User(
            "test-webhook-user-" + suffix,
            "test-webhook-user-" + suffix + "@example.com",
            "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                987654321L,
                "test-repository-user",
                "Test Repository User",
                "https://github.com/images/test.png",
                "test-access-token"
        );

        GitHubAccount savedAccount =
            githubAccountRepository.saveAndFlush(githubAccount);

        Instant repositoryCreatedAt = Instant.now().minusSeconds(7200);
        Instant repositoryUpdatedAt = Instant.now().minusSeconds(3600);

        Repository repository = new Repository(
            savedAccount,
                100000001L,
                "projectsmaneger",
                "test-repository-user/projectsmaneger",
                "Test project",
                "https://github.com/test-repository-user/projectsmaneger",
                "main",
                false,
                false,
                repositoryCreatedAt,
                repositoryUpdatedAt,
                repositoryUpdatedAt
        );

        Repository savedRepository =
            repositoryRepository.saveAndFlush(repository);

        ObjectMapper objectMapper = new ObjectMapper();

        String json = "{\"action\":\"created\",\"repository\":\"projectsmaneger\"}";

        JsonNode payload = objectMapper.readTree(json);

        WebhookEvent webhookEvent = new WebhookEvent(
            savedRepository,
            "delivery-001",
            "pull_request",
            "created",
            payload,
            WebhookEvent.ProcessingStatus.RECEIVED,
            null
        );

        WebhookEvent savedWebhookEvent = 
            webhookEventRepository.saveAndFlush(webhookEvent);

        assertNotNull(savedWebhookEvent.getId());
    
        Optional<WebhookEvent> foundByDeliveryId =
            webhookEventRepository.findByGithubDeliveryId("delivery-001");
    
        assertTrue(foundByDeliveryId.isPresent());
    }
}
