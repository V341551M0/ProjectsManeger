package com.projectsmaneger.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectUpdateRequest(
    @NotBlank String name, String description, Project.Status status) {}
