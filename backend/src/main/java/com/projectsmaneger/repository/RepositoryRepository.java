package com.projectsmaneger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {

    List<Repository> findByGithubAccountId(Long githubAccountId);

    Optional<Repository> findByGithubRepositoryId(Long githubRepositoryId);
}
