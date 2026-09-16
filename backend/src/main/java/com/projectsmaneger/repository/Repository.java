package com.projectsmaneger.repository;

import java.time.Instant;

import com.projectsmaneger.github.GitHubAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "repositories")
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "github_account_id", nullable = false)
    private GitHubAccount githubAccount;

    @Column(name = "github_repository_id", nullable = false, unique = true)
    private Long githubRepositoryId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "full_name", nullable = false, length = 500)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "default_branch", length = 255)
    private String defaultBranch;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "is_archived", nullable = false)
    private boolean isArchived = false;

    @Column(name = "github_created_at")
    private Instant githubCreatedAt;

    @Column(name = "github_updated_at")
    private Instant githubUpdatedAt;

    @Column(name = "synced_at")
    private Instant syncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Repository() {
        // Construtor exigido pelo JPA.
    }

    public Repository(
            GitHubAccount githubAccount,
            Long githubRepositoryId,
            String name,
            String fullName,
            String description,
            String url,
            String defaultBranch,
            boolean isPrivate,
            boolean isArchived,
            Instant githubCreatedAt,
            Instant githubUpdatedAt,
            Instant syncedAt
    ) {
        this.githubAccount = githubAccount;
        this.githubRepositoryId = githubRepositoryId;
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.url = url;
        this.defaultBranch = defaultBranch;
        this.isPrivate = isPrivate;
        this.isArchived = isArchived;
        this.githubCreatedAt = githubCreatedAt;
        this.githubUpdatedAt = githubUpdatedAt;
        this.syncedAt = syncedAt;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public GitHubAccount getGithubAccount() {
        return githubAccount;
    }

    public Long getGithubRepositoryId() {
        return githubRepositoryId;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public Instant getGithubCreatedAt() {
        return githubCreatedAt;
    }

    public Instant getGithubUpdatedAt() {
        return githubUpdatedAt;
    }

    public Instant getSyncedAt() {
        return syncedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setGithubAccount(GitHubAccount githubAccount) {
        this.githubAccount = githubAccount;
    }

    public void setGithubRepositoryId(Long githubRepositoryId) {
        this.githubRepositoryId = githubRepositoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public void setGithubCreatedAt(Instant githubCreatedAt) {
        this.githubCreatedAt = githubCreatedAt;
    }

    public void setGithubUpdatedAt(Instant githubUpdatedAt) {
        this.githubUpdatedAt = githubUpdatedAt;
    }

    public void setSyncedAt(Instant syncedAt) {
        this.syncedAt = syncedAt;
    }
}
