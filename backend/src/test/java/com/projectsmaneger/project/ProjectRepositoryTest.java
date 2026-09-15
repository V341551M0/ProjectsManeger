package com.projectsmaneger.project;

import java.util.List;
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
class ProjectRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldFindProjectsByOwnerId() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        User user = new User(
                "test-project-user-" + suffix,
                "test-project-user-" + suffix + "@example.com",
                "hashed-password"
        );

        User savedUser = userRepository.saveAndFlush(user);

        Project project = new Project(
                savedUser,
                "Test Project",
                "Project created for repository test"
        );

        Project savedProject = projectRepository.saveAndFlush(project);

        List<Project> projects =
                projectRepository.findByOwnerId(savedUser.getId());

        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getId())
                .isEqualTo(savedProject.getId());
        assertThat(projects.get(0).getName())
                .isEqualTo("Test Project");
    }
}