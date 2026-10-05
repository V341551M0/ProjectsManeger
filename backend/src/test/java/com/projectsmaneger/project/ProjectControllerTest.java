package com.projectsmaneger.project;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.projectsmaneger.security.JwtService;
import com.projectsmaneger.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProjectService projectService;

  @MockBean private JwtService jwtService;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @Test
  @WithMockUser(username = "verissimo")
  void shouldReturnMyProject() throws Exception {

    Project project = new Project(null, "ProjectsManeger", "Personal project manager");

    when(projectService.findMyProject("verissimo", 1L)).thenReturn(project);

    mockMvc
        .perform(get("/api/projects/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("ProjectsManeger"))
        .andExpect(jsonPath("$.description").value("Personal project manager"));

    verify(projectService).findMyProject("verissimo", 1L);
  }

  @Test
  @WithMockUser(username = "verissimo")
  void shouldUpdateMyProject() throws Exception {

    Project project = new Project(null, "ProjectsManeger Updated", "Updated project description");

    project.setStatus(Project.Status.ACTIVE);

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
}
