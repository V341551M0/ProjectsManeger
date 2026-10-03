package com.projectsmaneger.project;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private User owner;
    private User anotherUser;
    private Project project;

    @BeforeEach
    void setUp() {

        owner = spy(new User(
                "verissimo",
                "verissimo@test.local",
                "password-hash"
        ));

        anotherUser = spy(new User(
                "another",
                "another@test.local",
                "password-hash"
        ));

        project = new Project(
                owner,
                "ProjectsManeger",
                "Personal project manager"
        );
    }

    @Test
    void shouldFindProjectWhenItBelongsToAuthenticatedUser() {

        when(owner.getId()).thenReturn(1L);

        when(userRepository.findByUsername("verissimo"))
                .thenReturn(Optional.of(owner));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        Project result = projectService.findMyProject(
                "verissimo",
                1L
        );

        assertNotNull(result);
        assertEquals(project, result);

        verify(userRepository).findByUsername("verissimo");
        verify(projectRepository).findById(1L);
    }

    @Test
    void shouldRejectProjectWhenItBelongsToAnotherUser() {

        when(owner.getId()).thenReturn(1L);
        when(anotherUser.getId()).thenReturn(2L);

        project.setOwner(anotherUser);

        when(userRepository.findByUsername("verissimo"))
                .thenReturn(Optional.of(owner));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> projectService.findMyProject(
                                "verissimo",
                                1L
                        )
                );

        assertEquals("Project not found", exception.getMessage());

        verify(userRepository).findByUsername("verissimo");
        verify(projectRepository).findById(1L);
    }

    @Test
    void shouldRejectProjectWhenItDoesNotExist() {

        when(userRepository.findByUsername("verissimo"))
                .thenReturn(Optional.of(owner));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> projectService.findMyProject(
                                "verissimo",
                                1L
                        )
                );

        assertEquals("Project not found", exception.getMessage());

        verify(userRepository).findByUsername("verissimo");
        verify(projectRepository).findById(1L);
    }
}