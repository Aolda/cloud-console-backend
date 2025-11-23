package com.acc.local.repository.adapters;

import com.acc.local.entity.ProjectEntity;
import com.acc.local.repository.jpa.ProjectJpaRepository;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;

    @Override
    public Optional<ProjectEntity> findById(String projectId) {
        return projectJpaRepository.findById(projectId);
    }

    @Override
    public void save(ProjectEntity projectEntity) {
        projectJpaRepository.save(projectEntity);
    }
}
