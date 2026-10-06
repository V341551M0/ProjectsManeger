package com.projectsmaneger.project;

import java.time.Instant;

public record ProjectResponse(
    Long id,
    String name,
    String description,
    Project.Status status,
    Instant createdAt,
    Instant updatedAt) {}
