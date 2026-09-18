package com.projectsmaneger.pullrequest;

import java.time.Instant;

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

@Entity
@Table(name = "pull_requests")
public class PullRequest {

    public enum State {
        OPEN,
        CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "github_pull_request_id", nullable = false)
    private Long githubPullRequestId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "author_login", length = 255)
    private String authorLogin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private State state;

    @Column(name = "created_at_github", nullable = false)
    private Instant createdAtGithub;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "merged_at")
    private Instant mergedAt;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "source_branch", length = 255)
    private String sourceBranch;

    @Column(name = "target_branch", length = 255)
    private String targetBranch;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PullRequest() {
        // Construtor exigido pelo JPA.
    }

    public PullRequest(
            Repository repository,
            Long githubPullRequestId,
            String title,
            String authorLogin,
            State state,
            Instant createdAtGithub,
            Instant closedAt,
            Instant mergedAt,
            String url,
            String sourceBranch,
            String targetBranch
    ) {
        this.repository = repository;
        this.githubPullRequestId = githubPullRequestId;
        this.title = title;
        this.authorLogin = authorLogin;
        this.state = state;
        this.createdAtGithub = createdAtGithub;
        this.closedAt = closedAt;
        this.mergedAt = mergedAt;
        this.url = url;
        this.sourceBranch = sourceBranch;
        this.targetBranch = targetBranch;
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

    public Long getGithubPullRequestId() {
        return githubPullRequestId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public State getState() {
        return state;
    }

    public Instant getCreatedAtGithub() {
        return createdAtGithub;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public Instant getMergedAt() {
        return mergedAt;
    }

    public String getUrl() {
        return url;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
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

    public void setGithubPullRequestId(Long githubPullRequestId) {
        this.githubPullRequestId = githubPullRequestId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setCreatedAtGithub(Instant createdAtGithub) {
        this.createdAtGithub = createdAtGithub;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public void setMergedAt(Instant mergedAt) {
        this.mergedAt = mergedAt;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }
}
