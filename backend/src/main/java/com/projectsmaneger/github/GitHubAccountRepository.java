package com.projectsmaneger.github;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubAccountRepository extends JpaRepository<GitHubAccount, Long> {

    Optional<GitHubAccount> findByUserId(Long userId);

    Optional<GitHubAccount> findByGithubUserId(Long githubUserId);

    Optional<GitHubAccount> findByGithubLogin(String githubLogin);
}
