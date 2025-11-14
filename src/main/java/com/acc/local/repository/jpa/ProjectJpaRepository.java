package com.acc.local.repository.jpa;

import com.acc.local.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, String> {
}
