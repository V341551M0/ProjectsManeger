package com.projectsmaneger.auth;

import com.projectsmaneger.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        String token = authService.authenticate(
                request.username(),
                request.password()
        );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }
}
