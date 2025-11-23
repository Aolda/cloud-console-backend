package com.acc.local.repository.ports;

import java.util.*;

import com.acc.local.entity.ProjectParticipantEntity;

public interface ProjectParticipantRepositoryPort {

	List<ProjectParticipantEntity> findByProjectId(String projectID);

	List<ProjectParticipantEntity> findByParticipantId(String userId);

	void save(ProjectParticipantEntity projectParticipantEntity);
}
