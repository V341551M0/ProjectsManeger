package com.projectsmaneger.pullrequest;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestRepository 
    extends JpaRepository<PullRequest, Long> {
        List<PullRequest> findByRepositoryId(Long repositoryId);

        Optional<PullRequest> findByRepositoryIdAndGithubPullRequestId(
            Long repositoryId,
            Long githubPullRequestId
        );
    }