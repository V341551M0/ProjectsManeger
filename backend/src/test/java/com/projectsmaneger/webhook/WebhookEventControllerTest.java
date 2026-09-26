package com.projectsmaneger.webhook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.projectsmaneger.security.UserDetailsServiceImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.projectsmaneger.security.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookEventController.class)
class WebhookEventControllerTest {

    @org.springframework.beans.factory.annotation.Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebhookEventService webhookEventService;

    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReceiveGithubWebhook() throws Exception {
        String payload = """
                {
                    "action": "opened",
                    "repository": {
                        "id": 12345,
                        "name": "projectsmaneger"
                    }
                }
                """;

        mockMvc.perform(
                post("/api/webhooks/github")
                        .with(csrf())
                        .with(user("github-webhook"))
                        .header("X-GitHub-Delivery", "delivery-controller-001")
                        .header("X-GitHub-Event", "pull_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isOk());

        verify(webhookEventService).registerEvent(
                eq(12345L),
                eq("delivery-controller-001"),
                eq("pull_request"),
                eq("opened"),
                any()
        );
    }
}