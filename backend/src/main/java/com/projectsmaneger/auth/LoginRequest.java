package com.projectsmaneger.auth;

public record LoginRequest (
    String username,
    String password
) {
    
}
