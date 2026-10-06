package com.projectsmaneger.user;

import java.time.Instant;

public record UserResponse(
    Long id,
    String username,
    String email,
    boolean enabled,
    Instant createdAt,
    Instant updatedAt) {}
