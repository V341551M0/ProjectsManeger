package com.projectsmaneger.project;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> findMyProjects(
            Authentication authentication
    ) {
        String username = authentication.getName();

        return ResponseEntity.ok(
                projectService.findMyProjects(username)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findMyProject(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String username = authentication.getName();

        Project project = projectService.findMyProject(
                username,
                id
        );

        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<Project> create(
            Authentication authentication,
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        String username = authentication.getName();

        Project project = projectService.create(
                username,
                request.name(),
                request.description()
        );

        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        String username = authentication.getName();

        Project project = projectService.update(
                username,
                id,
                request.name(),
                request.description(),
                request.status()
        );  
    
    return ResponseEntity.ok(project);
    }
}