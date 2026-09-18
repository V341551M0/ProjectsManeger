package com.projectsmaneger.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.projectsmaneger.repository.Repository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "webhook_events")
public class WebhookEvent {

    public enum ProcessingStatus {
        RECEIVED,
        PROCESSED,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @Column(name = "github_delivery_id", nullable = false, unique = true, length = 255)
    private String githubDeliveryId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(length = 100)
    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode payload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 30)
    private ProcessingStatus processingStatus;

    @Column(name = "processing_error")
    private String processingError;

    protected WebhookEvent() {
        // Construtor exigido pelo JPA.
    }

    public WebhookEvent(
            Repository repository,
            String githubDeliveryId,
            String eventType,
            String action,
            JsonNode payload,
            ProcessingStatus processingStatus,
            String processingError
    ) {
        this.repository = repository;
        this.githubDeliveryId = githubDeliveryId;
        this.eventType = eventType;
        this.action = action;
        this.payload = payload;
        this.processingStatus = processingStatus;
        this.processingError = processingError;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (receivedAt == null) {
            receivedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Reservado para futuras regras de atualização.
    }

    public Long getId() {
        return id;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getGithubDeliveryId() {
        return githubDeliveryId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAction() {
        return action;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public String getProcessingError() {
        return processingError;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setGithubDeliveryId(String githubDeliveryId) {
        this.githubDeliveryId = githubDeliveryId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public void setProcessingError(String processingError) {
        this.processingError = processingError;
    }
}