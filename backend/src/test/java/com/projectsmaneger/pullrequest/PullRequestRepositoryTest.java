package com.projectsmaneger.pullrequest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.projectsmaneger.github.GitHubAccount;
import com.projectsmaneger.github.GitHubAccountRepository;
import com.projectsmaneger.repository.Repository;
import com.projectsmaneger.repository.RepositoryRepository;
import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PullRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Test
    void shouldPersistAndFindPullRequestsByRepositoryId() {
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User(
                "test-pr-user-" + suffix,
                "test-pr-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                777000000L + Math.abs(
                        UUID.randomUUID().getLeastSignificantBits() % 1000000
                ),
                "test-pr-user",
                "Test Pull Request User",
                "https://github.com/images/test.png",
                "test-access-token"
        );

        GitHubAccount savedAccount =
                githubAccountRepository.saveAndFlush(githubAccount);

        Instant repositoryCreatedAt =
                Instant.now().minusSeconds(7200);

        Instant repositoryUpdatedAt =
                Instant.now().minusSeconds(3600);

        Repository repository = new Repository(
                savedAccount,
                888000000L + Math.abs(
                        UUID.randomUUID().getLeastSignificantBits() % 1000000
                ),
                "pull-request-test-repository",
                "test-pr-user/pull-request-test-repository",
                "Repository for pull request test",
                "https://github.com/test-pr-user/pull-request-test-repository",
                "main",
                false,
                false,
                repositoryCreatedAt,
                repositoryUpdatedAt,
                repositoryUpdatedAt
        );

        Repository savedRepository =
                repositoryRepository.saveAndFlush(repository);

        Instant firstCreatedAt =
                Instant.now().minusSeconds(7200);

        Instant firstClosedAt =
                Instant.now().minusSeconds(3600);

        Instant firstMergedAt =
                Instant.now().minusSeconds(1800);

        PullRequest mergedPullRequest = new PullRequest(
                savedRepository,
                101L,
                "Implement project dashboard",
                "test-pr-user",
                PullRequest.State.CLOSED,
                firstCreatedAt,
                firstClosedAt,
                firstMergedAt,
                "https://github.com/test-pr-user/pull-request-test-repository/pull/101",
                "feature/dashboard",
                "main"
        );

        Instant secondCreatedAt =
                Instant.now().minusSeconds(5400);

        Instant secondClosedAt =
                Instant.now().minusSeconds(2700);

        PullRequest closedPullRequest = new PullRequest(
            savedRepository,
            102L,
            "Fix project status",
            "test-pr-user",
            PullRequest.State.CLOSED,
            secondCreatedAt,
            secondClosedAt,
            null,
            "https://github.com/test-pr-user/pull-request-test-repository/pull/102",
            "fix/project-status",
            "main"
        );

        PullRequest savedMergedPullRequest =
                pullRequestRepository.saveAndFlush(mergedPullRequest);

        PullRequest savedClosedPullRequest =
                pullRequestRepository.saveAndFlush(closedPullRequest);

        assertThat(savedMergedPullRequest.getId())
                .isNotNull();

        assertThat(savedClosedPullRequest.getId())
                .isNotNull();

        assertThat(savedMergedPullRequest.getRepository().getId())
                .isEqualTo(savedRepository.getId());

        assertThat(savedMergedPullRequest.getGithubPullRequestId())
                .isEqualTo(101L);

        assertThat(savedMergedPullRequest.getState())
                .isEqualTo(PullRequest.State.CLOSED);

        assertThat(savedMergedPullRequest.getMergedAt())
                .isEqualTo(firstMergedAt);

        assertThat(savedClosedPullRequest.getGithubPullRequestId())
                .isEqualTo(102L);

        assertThat(savedClosedPullRequest.getMergedAt())
                .isNull();

        var pullRequests =
                pullRequestRepository.findByRepositoryId(
                        savedRepository.getId()
                );

        assertThat(pullRequests)
                .hasSize(2);

        assertThat(pullRequests)
                .extracting(PullRequest::getGithubPullRequestId)
                .containsExactlyInAnyOrder(101L, 102L);

        var found =
                pullRequestRepository
                        .findByRepositoryIdAndGithubPullRequestId(
                                savedRepository.getId(),
                                101L
                        );

        assertThat(found)
                .isPresent();

        assertThat(found.get().getTitle())
                .isEqualTo("Implement project dashboard");

        assertThat(found.get().getSourceBranch())
                .isEqualTo("feature/dashboard");

        assertThat(found.get().getTargetBranch())
                .isEqualTo("main");
    }
}
