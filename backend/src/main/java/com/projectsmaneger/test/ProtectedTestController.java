package com.projectsmaneger.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ProtectedTestController {
    
    @GetMapping("/protected")
    public ResponseEntity<String> protectedRoute() {
        return ResponseEntity.ok("Autenticaçao JWT funcionando.");
    }
}
