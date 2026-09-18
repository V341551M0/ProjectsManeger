package com.projectsmaneger.project;

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
class ProjectRepositoryLinkTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubAccountRepository githubAccountRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectRepositoryLinkRepository projectRepositoryLinkRepository;

    @Test
    void shouldPersistAndFindLinksByProjectId() {
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User(
                "test-link-user-" + suffix,
                "test-link-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        Project project = new Project(
                savedUser,
                "Test Project " + suffix,
                "Project for repository link test"
        );

        Project savedProject = projectRepository.saveAndFlush(project);

        GitHubAccount githubAccount = new GitHubAccount(
                savedUser,
                111000000L + Math.abs(UUID.randomUUID().getLeastSignificantBits() % 1000000),
                "test-link-user",
                "Test Link User",
                "https://github.com/images/test.png",
                "test-access-token"
        );

        GitHubAccount savedAccount =
                githubAccountRepository.saveAndFlush(githubAccount);

        Instant now = Instant.now();

        Repository repository = new Repository(
                savedAccount,
                222000000L + Math.abs(UUID.randomUUID().getLeastSignificantBits() % 1000000),
                "test-repository",
                "test-link-user/test-repository",
                "Repository for link test",
                "https://github.com/test-link-user/test-repository",
                "main",
                false,
                false,
                now,
                now,
                now
        );

        Repository savedRepository =
                repositoryRepository.saveAndFlush(repository);

        ProjectRepositoryLink link = new ProjectRepositoryLink(
                savedProject,
                savedRepository,
                ProjectRepositoryLink.Role.PRIMARY,
                true,
                0
        );

        ProjectRepositoryLink savedLink =
                projectRepositoryLinkRepository.saveAndFlush(link);

        assertThat(savedLink.getId()).isNotNull();
        assertThat(savedLink.getProject().getId())
                .isEqualTo(savedProject.getId());
        assertThat(savedLink.getRepository().getId())
                .isEqualTo(savedRepository.getId());
        assertThat(savedLink.getRole())
                .isEqualTo(ProjectRepositoryLink.Role.PRIMARY);
        assertThat(savedLink.isPrimary())
                .isTrue();
        assertThat(savedLink.getDisplayOrder())
                .isZero();
        assertThat(savedLink.getLinkedAt())
                .isNotNull();

        var foundLinks =
                projectRepositoryLinkRepository.findByProjectId(
                        savedProject.getId()
                );

        assertThat(foundLinks)
                .hasSize(1);

        assertThat(foundLinks.get(0).getId())
                .isEqualTo(savedLink.getId());

        assertThat(foundLinks.get(0).getRepository().getId())
                .isEqualTo(savedRepository.getId());
    }
}