package com.projectsmaneger.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepositoryLinkRepository
        extends JpaRepository<ProjectRepositoryLink, Long> {

    List<ProjectRepositoryLink> findByProjectId(Long projectId);
}