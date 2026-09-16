package com.projectsmaneger.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.projectsmaneger.github.GitHubAccount;
import com.projectsmaneger.github.GitHubAccountRepository;
import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Test
    void shouldPersistAndFindRepositoriesByGithubAccountId() {
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User(
                "test-repository-user-" + suffix,
                "test-repository-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                987654321L,
                "test-repository-user",
                "Test Repository User",
                "https://github.com/images/test.png",
                "test-access-token"
        );

        GitHubAccount savedAccount =
                githubAccountRepository.saveAndFlush(githubAccount);

        Instant now = Instant.now();

        Repository first = new Repository(
                savedAccount,
                100000001L,
                "projectsmaneger",
                "test-repository-user/projectsmaneger",
                "Test project",
                "https://github.com/test-repository-user/projectsmaneger",
                "main",
                false,
                false,
                now,
                now,
                now
        );

        Repository second = new Repository(
                savedAccount,
                100000002L,
                "another-project",
                "test-repository-user/another-project",
                "Another project",
                "https://github.com/test-repository-user/another-project",
                "main",
                true,
                false,
                now,
                now,
                now
        );

        Repository savedFirst =
                repositoryRepository.saveAndFlush(first);

        Repository savedSecond =
                repositoryRepository.saveAndFlush(second);

        List<Repository> repositories =
                repositoryRepository.findByGithubAccountId(
                        savedAccount.getId()
                );

        assertThat(repositories)
                .hasSize(2);

        assertThat(repositories)
                .extracting(Repository::getId)
                .containsExactlyInAnyOrder(
                        savedFirst.getId(),
                        savedSecond.getId()
                );

        assertThat(repositories)
                .extracting(Repository::getName)
                .containsExactlyInAnyOrder(
                        "projectsmaneger",
                        "another-project"
                );

        var found =
                repositoryRepository.findByGithubRepositoryId(
                        100000001L
                );

        assertThat(found).isPresent();

        assertThat(found.get().getId())
                .isEqualTo(savedFirst.getId());
    }
}
