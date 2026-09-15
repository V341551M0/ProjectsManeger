package com.projectsmaneger.github;

import java.time.Instant;

import com.projectsmaneger.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "github_accounts")
public class GitHubAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "github_user_id", nullable = false, unique = true)
    private Long githubUserId;

    @Column(name = "github_login", nullable = false, length = 100)
    private String githubLogin;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected GitHubAccount() {
        // Construtor exigido pelo JPA.
    }

    public GitHubAccount(
            User user,
            Long githubUserId,
            String githubLogin,
            String displayName,
            String avatarUrl,
            String accessToken
    ) {
        this.user = user;
        this.githubUserId = githubUserId;
        this.githubLogin = githubLogin;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.accessToken = accessToken;
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

    public User getUser() {
        return user;
    }

    public Long getGithubUserId() {
        return githubUserId;
    }

    public String getGithubLogin() {
        return githubLogin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGithubUserId(Long githubUserId) {
        this.githubUserId = githubUserId;
    }

    public void setGithubLogin(String githubLogin) {
        this.githubLogin = githubLogin;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
