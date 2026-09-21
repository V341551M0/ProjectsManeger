package com.projectsmaneger.auth;

import com.projectsmaneger.security.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService; 

    @Test
    void shouldLoginUser() throws Exception {

        Authentication authentication =
            org.mockito.Mockito.mock(Authentication.class);
        
        when(authService.authenticate(any(String.class), any(String.class)))
            .thenReturn(authentication);

        mockMvc.perform(
            post("/api/auth/login")
                .with(csrf())
                .with(user("verissimo"))
                .contentType("application/json")
                .content("""
                                {
                                    "username": "verissimo",
                                    "password": "senha"
                                }
                                """)
        ).andExpect(status().isOk());
    }
}
