package com.projectsmaneger.project;

import java.time.Instant;

import com.projectsmaneger.repository.Repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_repositories")
public class ProjectRepositoryLink {

    public enum Role {
        PRIMARY,
        SECONDARY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.SECONDARY;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "linked_at", nullable = false, updatable = false)
    private Instant linkedAt;

    protected ProjectRepositoryLink() {
        // Construtor exigido pelo JPA.
    }

    public ProjectRepositoryLink(
            Project project,
            Repository repository,
            Role role,
            boolean isPrimary,
            Integer displayOrder
    ) {
        this.project = project;
        this.repository = repository;
        this.role = role;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        linkedAt = now;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public Repository getRepository() {
        return repository;
    }

    public Role getRole() {
        return role;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}