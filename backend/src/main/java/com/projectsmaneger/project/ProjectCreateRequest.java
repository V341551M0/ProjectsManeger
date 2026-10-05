package com.projectsmaneger.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectCreateRequest(@NotBlank String name, String description) {}
