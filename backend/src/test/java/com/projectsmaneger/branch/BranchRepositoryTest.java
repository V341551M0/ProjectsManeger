package com.projectsmaneger.branch;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.projectsmaneger.commit.Commit;
import com.projectsmaneger.commit.CommitRepository;
import com.projectsmaneger.github.GitHubAccount;
import com.projectsmaneger.github.GitHubAccountRepository;
import com.projectsmaneger.repository.Repository;
import com.projectsmaneger.repository.RepositoryRepository;
import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BranchRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Test
    void shouldPersistAndFindBranchesByRepositoryId() {
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User(
                "test-branch-user-" + suffix,
                "test-branch-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                555000000L + Math.abs(
                        UUID.randomUUID().getLeastSignificantBits() % 1000000
                ),
                "test-branch-user",
                "Test Branch User",
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
                666000000L + Math.abs(
                        UUID.randomUUID().getLeastSignificantBits() % 1000000
                ),
                "branch-test-repository",
                "test-branch-user/branch-test-repository",
                "Repository for branch test",
                "https://github.com/test-branch-user/branch-test-repository",
                "main",
                false,
                false,
                repositoryCreatedAt,
                repositoryUpdatedAt,
                repositoryUpdatedAt
        );

        Repository savedRepository =
                repositoryRepository.saveAndFlush(repository);

        Instant commitTime = Instant.now();

        Commit commit = new Commit(
                savedRepository,
                "cccccccccccccccccccccccccccccccccccccccc",
                "Test Branch User",
                "test@example.com",
                "Commit for branch test",
                "main",
                "https://github.com/test-branch-user/branch-test-repository/commit/cccccccc",
                commitTime
        );

        Commit savedCommit =
                commitRepository.saveAndFlush(commit);

        Branch branch = new Branch(
                savedRepository,
                "main",
                true,
                savedCommit,
                commitTime
        );

        Branch savedBranch =
                branchRepository.saveAndFlush(branch);

        assertThat(savedBranch.getId()).isNotNull();

        assertThat(savedBranch.getRepository().getId())
                .isEqualTo(savedRepository.getId());

        assertThat(savedBranch.getName())
                .isEqualTo("main");

        assertThat(savedBranch.isDefault())
                .isTrue();

        assertThat(savedBranch.getLatestCommit())
                .isNotNull();

        assertThat(savedBranch.getLatestCommit().getId())
                .isEqualTo(savedCommit.getId());

        assertThat(savedBranch.getLastUpdatedAt())
                .isEqualTo(commitTime);

        assertThat(savedBranch.getCreatedAt())
                .isNotNull();

        assertThat(savedBranch.getUpdatedAt())
                .isNotNull();

        var branches =
                branchRepository.findByRepositoryId(
                        savedRepository.getId()
                );

        assertThat(branches)
                .hasSize(1);

        assertThat(branches.get(0).getId())
                .isEqualTo(savedBranch.getId());

        assertThat(branches.get(0).getName())
                .isEqualTo("main");
    }
}
