package com.projectsmaneger.github;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GitHubAccountRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Test
    void shouldPersistAndFindGitHubAccountByUserId() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        User user = new User(
                "test-github-user-" + suffix,
                "test-github-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                123456789L,
                "test-github-user",
                "Test GitHub User",
                "https://github.com/images/test.png",
                "test-access-token"
        );

        GitHubAccount savedAccount =
                githubAccountRepository.saveAndFlush(githubAccount);

        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getUser().getId())
                .isEqualTo(savedUser.getId());
        assertThat(savedAccount.getGithubUserId())
                .isEqualTo(123456789L);
        assertThat(savedAccount.getGithubLogin())
                .isEqualTo("test-github-user");

        var foundAccount =
                githubAccountRepository.findByUserId(savedUser.getId());

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getId())
                .isEqualTo(savedAccount.getId());
        assertThat(foundAccount.get().getGithubLogin())
                .isEqualTo("test-github-user");
    }
}
