package com.projectsmaneger.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubUserResponse(
    Long id, String login, String name, @JsonProperty("avatar_url") String avatarUrl) {}
