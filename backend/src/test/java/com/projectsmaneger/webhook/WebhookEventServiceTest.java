package com.projectsmaneger.webhook;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsmaneger.repository.Repository;
import com.projectsmaneger.repository.RepositoryRepository;

@ExtendWith(MockitoExtension.class)
class WebhookEventServiceTest {

    @Mock
    private WebhookEventRepository webhookEventRepository;

    @Mock
    private RepositoryRepository repositoryRepository;

    @InjectMocks
    private WebhookEventService webhookEventService;

    @Test
    void shouldRegisterWebhookEvent() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode payload = objectMapper.readTree(
                "{\"action\":\"created\",\"repository\":\"projectsmaneger\"}"
        );

        Repository repository = org.mockito.Mockito.mock(Repository.class);

        when(repositoryRepository.findByGithubRepositoryId(12345L))
                .thenReturn(Optional.of(repository));

        WebhookEvent savedWebhookEvent = new WebhookEvent(
                repository,
                "delivery-service-001",
                "pull_request",
                "created",
                payload,
                WebhookEvent.ProcessingStatus.RECEIVED,
                null
        );

        when(webhookEventRepository.save(any(WebhookEvent.class)))
                .thenReturn(savedWebhookEvent);

        WebhookEvent result = webhookEventService.registerEvent(
                12345L,
                "delivery-service-001",
                "pull_request",
                "created",
                payload
        );

        assertNotNull(result);
        assertEquals(
                "delivery-service-001",
                result.getGithubDeliveryId()
        );
        assertEquals(
                "pull_request",
                result.getEventType()
        );
        assertEquals(
                "created",
                result.getAction()
        );
        assertEquals(
                WebhookEvent.ProcessingStatus.RECEIVED,
                result.getProcessingStatus()
        );

        verify(repositoryRepository)
                .findByGithubRepositoryId(12345L);

        verify(webhookEventRepository)
                .save(any(WebhookEvent.class));
    }
}
