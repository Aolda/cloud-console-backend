package com.acc.local.repository.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.acc.local.entity.ProjectParticipantEntity;
import com.acc.local.repository.jpa.ProjectParticipantJpaRepository;
import com.acc.local.repository.ports.ProjectParticipantRepositoryPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectParticipantRepositoryAdapter implements ProjectParticipantRepositoryPort {

	private final ProjectParticipantJpaRepository projectParticipantJpaRepository;

	@Override
	public List<ProjectParticipantEntity> findByProjectId(String projectId) {
		return projectParticipantJpaRepository.findByProjectId(projectId);
	}

	@Override
	public List<ProjectParticipantEntity> findByParticipantId(String userId) {
		return projectParticipantJpaRepository.findByUserId(userId);
	}

	@Override
	public Optional<ProjectParticipantEntity> findByProjectIdAndParticipantId(String projectId, String participantId) {
		return projectParticipantJpaRepository.findByProjectIdAndParticipantId(projectId, participantId);
	}

	@Override
	public void save(ProjectParticipantEntity projectParticipantEntity) {
		projectParticipantJpaRepository.save(projectParticipantEntity);
	}

	@Override
	public void saveAll(List<ProjectParticipantEntity> projectParticipantEntities) {
		projectParticipantJpaRepository.saveAll(projectParticipantEntities);
	}

	@Override
	public void deleteAll(List<ProjectParticipantEntity> projectParticipantEntities) {
		projectParticipantJpaRepository.deleteAll(projectParticipantEntities);
	}
}
