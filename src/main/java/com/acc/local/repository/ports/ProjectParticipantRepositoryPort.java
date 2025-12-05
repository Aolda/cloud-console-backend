package com.acc.local.repository.ports;

import java.util.*;

import javax.swing.text.html.Option;

import com.acc.local.entity.ProjectParticipantEntity;

public interface ProjectParticipantRepositoryPort {

	List<ProjectParticipantEntity> findByProjectId(String projectId);

	List<ProjectParticipantEntity> findByParticipantId(String userId);

	Optional<ProjectParticipantEntity> findByProjectIdAndParticipantId(String projectId, String participantId);

	void save(ProjectParticipantEntity projectParticipantEntity);

	void saveAll(List<ProjectParticipantEntity> projectParticipantEntities);

	void deleteAll(List<ProjectParticipantEntity> projectParticipantEntities);
}
