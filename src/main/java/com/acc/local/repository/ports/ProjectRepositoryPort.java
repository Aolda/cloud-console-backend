package com.acc.local.repository.ports;

import com.acc.local.entity.ProjectEntity;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Optional<ProjectEntity> findById(String projectId);
    void save(ProjectEntity projectEntity);
    List<ProjectEntity> findAll();
}
