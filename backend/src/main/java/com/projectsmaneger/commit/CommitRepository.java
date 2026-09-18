package com.projectsmaneger.commit;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<Commit, Long> {

    List<Commit> findByRepositoryId(Long repositoryId);

    Optional<Commit> findByRepositoryIdAndGithubSha(
            Long repositoryId,
            String githubSha
    );
}