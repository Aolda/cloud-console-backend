package com.acc.local.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acc.local.entity.ProjectParticipantEntity;

public interface ProjectParticipantJpaRepository extends JpaRepository<ProjectParticipantEntity, String> {

	@Query("SELECT pp FROM ProjectParticipantEntity pp WHERE pp.project.projectId = :projectId")
	List<ProjectParticipantEntity> findByProjectId(@Param("projectId") String projectId);

	@Query("SELECT pp FROM ProjectParticipantEntity pp WHERE pp.userDetail.userId = :participantId")
	List<ProjectParticipantEntity> findByUserId(@Param("participantId") String participantId);

	@Query("SELECT pp FROM ProjectParticipantEntity pp WHERE pp.project.projectId = :projectId AND pp.userDetail.userId = :participantId")
	Optional<ProjectParticipantEntity> findByProjectIdAndParticipantId(
		@Param("projectId") String projectId,
		@Param("participantId") String participantId
	);

	ProjectParticipantEntity save(ProjectParticipantEntity projectParticipantEntity);

}
