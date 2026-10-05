package com.projectsmaneger.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  @Mock private ProjectRepository projectRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private ProjectService projectService;

  private User owner;
  private User anotherUser;
  private Project project;

  @BeforeEach
  void setUp() {

    owner = spy(new User("verissimo", "verissimo@test.local", "password-hash"));

    anotherUser = spy(new User("another", "another@test.local", "password-hash"));

    project = new Project(owner, "ProjectsManeger", "Personal project manager");
  }

  @Test
  void shouldFindProjectWhenItBelongsToAuthenticatedUser() {

    when(owner.getId()).thenReturn(1L);

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    Project result = projectService.findMyProject("verissimo", 1L);

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

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> projectService.findMyProject("verissimo", 1L));

    assertEquals("Project not found", exception.getMessage());

    verify(userRepository).findByUsername("verissimo");
    verify(projectRepository).findById(1L);
  }

  @Test
  void shouldRejectProjectWhenItDoesNotExist() {

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> projectService.findMyProject("verissimo", 1L));

    assertEquals("Project not found", exception.getMessage());

    verify(userRepository).findByUsername("verissimo");
    verify(projectRepository).findById(1L);
  }

  @Test
  void shouldUpdateProjectWhenItBelongsToAuthenticatedUser() {

    when(owner.getId()).thenReturn(1L);

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    when(projectRepository.save(project)).thenReturn(project);

    Project result =
        projectService.update(
            "verissimo",
            1L,
            "ProjectsManeger Updated",
            "Updated project description",
            Project.Status.ACTIVE);

    assertNotNull(result);
    assertEquals("ProjectsManeger Updated", result.getName());
    assertEquals("Updated project description", result.getDescription());
    assertEquals(Project.Status.ACTIVE, result.getStatus());

    verify(userRepository).findByUsername("verissimo");
    verify(projectRepository).findById(1L);
    verify(projectRepository).save(project);
  }

  @Test
  void shouldRejectUpdateWhenProjectBelongsToAnotherUser() {

    when(owner.getId()).thenReturn(1L);
    when(anotherUser.getId()).thenReturn(2L);

    project.setOwner(anotherUser);

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                projectService.update(
                    "verissimo",
                    1L,
                    "ProjectsManeger Updated",
                    "Updated project description",
                    Project.Status.ACTIVE));

    assertEquals("Project not found", exception.getMessage());

    verify(userRepository).findByUsername("verissimo");
    verify(projectRepository).findById(1L);
  }

  @Test
  void shouldDeleteProjectWhenItBelongsToAuthenticatedUser() {

    when(owner.getId()).thenReturn(1L);

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    projectService.delete("verissimo", 1L);

    verify(userRepository).findByUsername("verissimo");
    verify(projectRepository).findById(1L);
    verify(projectRepository).delete(project);
  }

  @Test
  void shouldRejectDeleteWhenProjectBelongsToAnotherUser() {

    when(owner.getId()).thenReturn(1L);
    when(anotherUser.getId()).thenReturn(2L);

    project.setOwner(anotherUser);

    when(userRepository.findByUsername("verissimo")).thenReturn(Optional.of(owner));

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> projectService.delete("verissimo", 1L));

    assertEquals("Project not found", exception.getMessage());

    verify(userRepository).findByUsername("verissimo");
    verify(projectRepository).findById(1L);
    verify(projectRepository, never()).delete(project);
  }
}
