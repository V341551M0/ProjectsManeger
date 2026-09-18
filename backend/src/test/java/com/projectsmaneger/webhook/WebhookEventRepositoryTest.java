package com.projectsmaneger.webhook;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.projectsmaneger.github.GitHubAccountRepository;
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
    void shouldPersistAndFindWebhookEvents(){
        String suffix = UUID.randomUUID()
            .toString()
            .substring(0, 8);

        User user = new User(
            "test-webhook-user-" + suffix,
            "test-webhook-user-" + suffix + "@example.com",
            "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);
    }
}
