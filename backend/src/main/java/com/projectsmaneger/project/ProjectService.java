package com.projectsmaneger.project;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<Project> findMyProjects(String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + username
                        )
                );

        return projectRepository.findByOwnerId(owner.getId());
    }

    public Project create(
            String username,
            String name,
            String description
    ) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + username
                        )
                );

        Project project = new Project(
                owner,
                name,
                description
        );

        return projectRepository.save(project);
    }
}