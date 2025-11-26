package com.acc.local.repository.jpa;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.entity.ProjectRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProjectRequestJpaRepository extends JpaRepository<ProjectRequestEntity, String> {

	Page<ProjectRequestEntity> findByRequestUserId(String requestUserId, Pageable pageable);

	Page<ProjectRequestEntity> findByProjectRequestId(String projectRequestId, Pageable pageable);

	List<ProjectRequestEntity> findByProjectNameContaining(String keyword);

	Page<ProjectRequestEntity> findByProjectNameContaining(String keyword, Pageable pageable);

	@Transactional
	@Modifying
	@Query("UPDATE ProjectRequestEntity p SET p.status = :status, p.rejectReason = :rejectReason WHERE p.projectRequestId = :id")
	void updateStatusById(@Param("id") String id, @Param("status") ProjectRequestStatus status, @Param("rejectReason") String rejectReason);
}
