package com.projectsmaneger.branch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    List<Branch> findByRepositoryId(Long repositoryId);
}
