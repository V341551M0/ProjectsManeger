package com.projectsmaneger.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @WithMockUser(username = "verissimo")
    void shouldReturnMyProject() throws Exception {

        UserDetails user = User
                .withUsername("verissimo")
                .password("password")
                .roles("USER")
                .build();

        Project project = new Project(
                null,
                "ProjectsManeger",
                "Personal project manager"
        );

        when(projectService.findMyProject(
                "verissimo",
                1L
        )).thenReturn(project);

        mockMvc.perform(
                get("/api/projects/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("ProjectsManeger"))
                .andExpect(jsonPath("$.description")
                        .value("Personal project manager"));

        verify(projectService).findMyProject(
                "verissimo",
                1L
        );
    }
}