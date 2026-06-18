package com.acc.local.repository.jpa;

import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.dto.project.ProjectRequestDto;
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

	Optional<ProjectRequestEntity> findByProjectRequestId(String projectRequestId);

	List<ProjectRequestEntity> findByProjectNameContaining(String keyword);

	Page<ProjectRequestEntity> findByProjectNameContaining(String keyword, Pageable pageable);

	List<ProjectRequestEntity> findByProjectNameContainingOrderByProjectRequestIdAsc(String keyword, Pageable pageable);

	List<ProjectRequestEntity> findByProjectNameContainingAndProjectRequestIdGreaterThanOrderByProjectRequestIdAsc(
		String keyword,
		String projectRequestId,
		Pageable pageable
	);

	List<ProjectRequestEntity> findByProjectNameContainingAndProjectRequestIdLessThanOrderByProjectRequestIdDesc(
		String keyword,
		String projectRequestId,
		Pageable pageable
	);

	Page<ProjectRequestEntity> findByRequestUserIdAndProjectNameContaining(String requestUserId, String keyword, Pageable pageable);

	List<ProjectRequestEntity> findByRequestUserIdAndProjectNameContainingOrderByProjectRequestIdAsc(
		String requestUserId,
		String keyword,
		Pageable pageable
	);

	List<ProjectRequestEntity> findByRequestUserIdAndProjectNameContainingAndProjectRequestIdGreaterThanOrderByProjectRequestIdAsc(
		String requestUserId,
		String keyword,
		String projectRequestId,
		Pageable pageable
	);

	List<ProjectRequestEntity> findByRequestUserIdAndProjectNameContainingAndProjectRequestIdLessThanOrderByProjectRequestIdDesc(
		String requestUserId,
		String keyword,
		String projectRequestId,
		Pageable pageable
	);

	@Transactional
	@Modifying
	@Query("UPDATE ProjectRequestEntity p SET p.status = :status, p.rejectReason = :rejectReason WHERE p.projectRequestId = :id")
	void updateStatusById(@Param("id") String id, @Param("status") ProjectRequestStatus status, @Param("rejectReason") String rejectReason);

	List<ProjectRequestEntity> findByProjectNameContainingAndRequestUserId(String keyword, String requestUserId);
}
