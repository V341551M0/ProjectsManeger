package com.projectsmaneger.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.projectsmaneger.exception.ResourceNotFoundException;
import com.projectsmaneger.security.JwtService;
import com.projectsmaneger.security.UserDetailsServiceImpl;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = ProjectController.class,
    properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration")
class ProjectControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProjectService projectService;

  @MockBean private JwtService jwtService;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @Test
  @WithMockUser(username = "verissimo")
  void shouldRejectProjectCreationWhenNameIsBlank() throws Exception {

    mockMvc
        .perform(
            post("/api/projects")
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {
                        "name": "",
                        "description": "Invalid project"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.fields.name").value("must not be blank"));

    verify(projectService, never()).create(anyString(), anyString(), anyString());
  }

  @Test
  @WithMockUser(username = "verissimo")
  void shouldRejectProjectUpdateWhenNameIsBlank() throws Exception {

    mockMvc
        .perform(
            put("/api/projects/1")
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {
                        "name": "",
                        "description": "Invalid project",
                        "status": "ACTIVE"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.fields.name").value("must not be blank"));

    verify(projectService, never())
        .update(anyString(), anyLong(), anyString(), anyString(), any(Project.Status.class));
  }

  @Test
  @WithMockUser(username = "verissimo")
  void shouldReturnMyProject() throws Exception {

    ProjectResponse project =
        new ProjectResponse(
            1L,
            "ProjectsManeger",
            "Personal project manager",
            Project.Status.ACTIVE,
            Instant.now(),
            Instant.now());

    when(projectService.findMyProject("verissimo", 1L)).thenReturn(project);

    mockMvc
        .perform(get("/api/projects/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("ProjectsManeger"))
        .andExpect(jsonPath("$.description").value("Personal project manager"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));

    verify(projectService).findMyProject("verissimo", 1L);
  }

  @Test
  @WithMockUser(username = "verissimo")
  void shouldUpdateMyProject() throws Exception {

    ProjectResponse project =
        new ProjectResponse(
            1L,
            "ProjectsManeger Updated",
            "Updated project description",
            Project.Status.ACTIVE,
            Instant.now(),
            Instant.now());

    when(projectService.update(
            "verissimo",
            1L,
            "ProjectsManeger Updated",
            "Updated project description",
            Project.Status.ACTIVE))
        .thenReturn(project);

    mockMvc
        .perform(
            put("/api/projects/1")
                .with(csrf())
                .contentType("application/json")
                .content(
                    """
                    {
                        "name": "ProjectsManeger Updated",
                        "description": "Updated project description",
                        "status": "ACTIVE"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("ProjectsManeger Updated"))
        .andExpect(jsonPath("$.description").value("Updated project description"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));

    verify(projectService)
        .update(
            "verissimo",
            1L,
            "ProjectsManeger Updated",
            "Updated project description",
            Project.Status.ACTIVE);
  }

  @Test
  @WithMockUser(username = "verissimo")
  void shouldDeleteMyProject() throws Exception {

    doNothing().when(projectService).delete("verissimo", 1L);

    mockMvc.perform(delete("/api/projects/1").with(csrf())).andExpect(status().isNoContent());

    verify(projectService).delete("verissimo", 1L);
  }

  @Test
  @WithMockUser(username = "verissimo")
  void shouldReturnNotFoundWhenProjectDoesNotExist() throws Exception {

    when(projectService.findMyProject("verissimo", 1L))
        .thenThrow(new ResourceNotFoundException("Project not found"));

    mockMvc
        .perform(get("/api/projects/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Project not found"));

    verify(projectService).findMyProject("verissimo", 1L);
  }

  @Test
  void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {

    mockMvc.perform(get("/api/projects")).andExpect(status().isUnauthorized());

    verify(projectService, never()).findMyProjects(anyString());
  }
}
