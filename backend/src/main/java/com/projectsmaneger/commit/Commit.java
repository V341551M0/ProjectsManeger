package com.projectsmaneger.commit;

import java.time.Instant;

import com.projectsmaneger.repository.Repository;

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
@Table(name = "commits")
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "github_sha", nullable = false, length = 64)
    private String githubSha;

    @Column(name = "author_name", length = 255)
    private String authorName;

    @Column(name = "author_email", length = 255)
    private String authorEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "branch_name", length = 255)
    private String branchName;

    @Column(name = "commit_url", length = 500)
    private String commitUrl;

    @Column(name = "committed_at", nullable = false)
    private Instant committedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Commit() {
        // Construtor exigido pelo JPA.
    }

    public Commit(
            Repository repository,
            String githubSha,
            String authorName,
            String authorEmail,
            String message,
            String branchName,
            String commitUrl,
            Instant committedAt
    ) {
        this.repository = repository;
        this.githubSha = githubSha;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.message = message;
        this.branchName = branchName;
        this.commitUrl = commitUrl;
        this.committedAt = committedAt;
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

    public Repository getRepository() {
        return repository;
    }

    public String getGithubSha() {
        return githubSha;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public Instant getCommittedAt() {
        return committedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setGithubSha(String githubSha) {
        this.githubSha = githubSha;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setCommitUrl(String commitUrl) {
        this.commitUrl = commitUrl;
    }

    public void setCommittedAt(Instant committedAt) {
        this.committedAt = committedAt;
    }
}