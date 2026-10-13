package com.projectsmaneger.webhook;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.projectsmaneger.security.JwtService;
import com.projectsmaneger.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = WebhookEventController.class,
    properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration")
class WebhookEventControllerTest {

  @org.springframework.beans.factory.annotation.Autowired private MockMvc mockMvc;

  @MockBean private WebhookEventService webhookEventService;

  @MockBean private GitHubWebhookSignatureVerifier signatureVerifier;

  @MockBean private JwtService jwtService;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @Test
  void shouldReceiveGithubWebhook() throws Exception {
    String payload =
        """
                {
                    "action": "opened",
                    "repository": {
                        "id": 12345,
                        "name": "projectsmaneger"
                    }
                }
                """;

    when(signatureVerifier.isValid(
            any(byte[].class), org.mockito.ArgumentMatchers.eq("sha256=valid")))
        .thenReturn(true);

    mockMvc
        .perform(
            post("/api/webhooks/github")
                .with(csrf())
                .with(user("github-webhook"))
                .header("X-GitHub-Delivery", "delivery-controller-001")
                .header("X-GitHub-Event", "pull_request")
                .header("X-Hub-Signature-256", "sha256=valid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk());

    verify(webhookEventService)
        .registerEvent(
            eq(12345L), eq("delivery-controller-001"), eq("pull_request"), eq("opened"), any());
  }

  @Test
  void shouldRejectWebhookWithInvalidSignature() throws Exception {
    when(signatureVerifier.isValid(any(byte[].class), org.mockito.ArgumentMatchers.anyString()))
        .thenReturn(false);

    mockMvc
        .perform(
            post("/api/webhooks/github")
                .with(csrf())
                .header("X-GitHub-Delivery", "delivery-invalid")
                .header("X-GitHub-Event", "ping")
                .header("X-Hub-Signature-256", "sha256=invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"repository\":{\"id\":12345}}"))
        .andExpect(status().isUnauthorized());
  }
}
