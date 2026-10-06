package com.projectsmaneger.project;

import com.projectsmaneger.exception.ResourceNotFoundException;
import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  public List<ProjectResponse> findMyProjects(String username) {

    User owner = findUser(username);

    return projectRepository.findByOwnerId(owner.getId()).stream().map(this::toResponse).toList();
  }

  public ProjectResponse findMyProject(String username, Long projectId) {

    return toResponse(findProject(username, projectId));
  }

  private Project findProject(String username, Long projectId) {

    User owner = findUser(username);

    return projectRepository
        .findById(projectId)
        .filter(project -> project.getOwner().getId().equals(owner.getId()))
        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
  }

  public ProjectResponse create(String username, String name, String description) {

    User owner = findUser(username);

    Project project = new Project(owner, name, description);

    return toResponse(projectRepository.save(project));
  }

  public ProjectResponse update(
      String username, Long projectId, String name, String description, Project.Status status) {

    Project project = findProject(username, projectId);

    project.setName(name);
    project.setDescription(description);
    project.setStatus(status);

    return toResponse(projectRepository.save(project));
  }

  public void delete(String username, Long projectId) {

    Project project = findProject(username, projectId);

    projectRepository.delete(project);
  }

  private ProjectResponse toResponse(Project project) {

    return new ProjectResponse(
        project.getId(),
        project.getName(),
        project.getDescription(),
        project.getStatus(),
        project.getCreatedAt(),
        project.getUpdatedAt());
  }

  private User findUser(String username) {

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
  }
}
