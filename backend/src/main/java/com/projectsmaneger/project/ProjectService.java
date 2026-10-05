package com.projectsmaneger.project;

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

  public List<Project> findMyProjects(String username) {

    User owner = findUser(username);

    return projectRepository.findByOwnerId(owner.getId());
  }

  public Project findMyProject(String username, Long projectId) {

    User owner = findUser(username);

    return projectRepository
        .findById(projectId)
        .filter(project -> project.getOwner().getId().equals(owner.getId()))
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
  }

  public Project create(String username, String name, String description) {

    User owner = findUser(username);

    Project project = new Project(owner, name, description);

    return projectRepository.save(project);
  }

  public Project update(
      String username, Long projectId, String name, String description, Project.Status status) {

    Project project = findMyProject(username, projectId);

    project.setName(name);
    project.setDescription(description);
    project.setStatus(status);

    return projectRepository.save(project);
  }

  private User findUser(String username) {

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
  }

  public void delete(String username, Long projectId) {
    Project project = findMyProject(username, projectId);
    projectRepository.delete(project);
  }
}
