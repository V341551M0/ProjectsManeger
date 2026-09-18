package com.projectsmaneger.commit;

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
class CommitRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Test
    void shouldPersistAndFindCommitsByRepositoryId() {
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User(
                "test-commit-user-" + suffix,
                "test-commit-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                333000000L + Math.abs(
                        UUID.randomUUID().getLeastSignificantBits() % 1000000
                ),
                "test-commit-user",
                "Test Commit User",
                "https://github.com/images/test.png",
                "test-access-token"
        );

        GitHubAccount savedAccount =
                githubAccountRepository.saveAndFlush(githubAccount);

        Instant firstCommitTime = Instant.now().minusSeconds(3600);
        Instant secondCommitTime = Instant.now();

        Repository repository = new Repository(
                savedAccount,
                444000000L + Math.abs(
                        UUID.randomUUID().getLeastSignificantBits() % 1000000
                ),
                "commit-test-repository",
                "test-commit-user/commit-test-repository",
                "Repository for commit test",
                "https://github.com/test-commit-user/commit-test-repository",
                "main",
                false,
                false,
                firstCommitTime,
                secondCommitTime,
                secondCommitTime
        );

        Repository savedRepository =
                repositoryRepository.saveAndFlush(repository);

        Commit first = new Commit(
                savedRepository,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "Test Commit User",
                "test@example.com",
                "Initial commit",
                "main",
                "https://github.com/test-commit-user/commit-test-repository/commit/aaaaaaaa",
                firstCommitTime
        );

        Commit second = new Commit(
                savedRepository,
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                "Test Commit User",
                "test@example.com",
                "Implement project dashboard",
                "main",
                "https://github.com/test-commit-user/commit-test-repository/commit/bbbbbbbb",
                secondCommitTime
        );

        Commit savedFirst = commitRepository.saveAndFlush(first);
        Commit savedSecond = commitRepository.saveAndFlush(second);

        assertThat(savedFirst.getId()).isNotNull();
        assertThat(savedSecond.getId()).isNotNull();

        assertThat(savedFirst.getRepository().getId())
                .isEqualTo(savedRepository.getId());

        assertThat(savedFirst.getGithubSha())
                .isEqualTo(
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                );

        assertThat(savedFirst.getMessage())
                .isEqualTo("Initial commit");

        var commits =
                commitRepository.findByRepositoryId(
                        savedRepository.getId()
                );

        assertThat(commits)
                .hasSize(2);

        assertThat(commits)
                .extracting(Commit::getId)
                .containsExactlyInAnyOrder(
                        savedFirst.getId(),
                        savedSecond.getId()
                );

        var found =
                commitRepository.findByRepositoryIdAndGithubSha(
                        savedRepository.getId(),
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                );

        assertThat(found).isPresent();

        assertThat(found.get().getId())
                .isEqualTo(savedFirst.getId());

        assertThat(found.get().getMessage())
                .isEqualTo("Initial commit");
    }
}