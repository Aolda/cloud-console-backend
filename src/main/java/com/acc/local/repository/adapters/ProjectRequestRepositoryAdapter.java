package com.acc.local.repository.adapters;

import com.acc.global.common.PageRequest.Direction;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.entity.ProjectRequestEntity;
import com.acc.local.repository.jpa.ProjectRequestJpaRepository;
import com.acc.local.repository.ports.ProjectRequestRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRequestRepositoryAdapter implements ProjectRequestRepositoryPort {

	private final ProjectRequestJpaRepository projectRequestJpaRepository;

	private Pageable createPageable(int offset, int size) {
		// pageNumber는 0부터 시작합니다.
		int pageNumber = offset / size;
		return PageRequest.of(pageNumber, size);
	}

	private Pageable createCursorPageable(int size) {
		return PageRequest.of(0, Math.max(size, 1));
	}


	@Override
	public List<ProjectRequestEntity> findAllByKeyword(String keyword, String requestUserId) {
		return projectRequestJpaRepository.findByProjectNameContainingAndRequestUserId(keyword, requestUserId);
	}

	@Override
	public List<ProjectRequestEntity> findAllByKeyword(String keyword, String marker, Direction direction, int size) {
		Pageable pageable = createCursorPageable(size);
		if (direction == Direction.prev && marker != null) {
			return projectRequestJpaRepository
				.findByProjectNameContainingAndProjectRequestIdLessThanOrderByProjectRequestIdDesc(
					keyword, marker, pageable
				);
		}
		if (marker != null) {
			return projectRequestJpaRepository
				.findByProjectNameContainingAndProjectRequestIdGreaterThanOrderByProjectRequestIdAsc(
					keyword, marker, pageable
				);
		}
		return projectRequestJpaRepository.findByProjectNameContainingOrderByProjectRequestIdAsc(keyword, pageable);
	}

	@Override
	public List<ProjectRequestEntity> findAllByKeywordAndRequestUserId(
		String searchKeyword,
		String requestUserId,
		String marker,
		Direction direction,
		int size
	) {
		Pageable pageable = createCursorPageable(size);
		if (direction == Direction.prev && marker != null) {
			return projectRequestJpaRepository
				.findByRequestUserIdAndProjectNameContainingAndProjectRequestIdLessThanOrderByProjectRequestIdDesc(
					requestUserId, searchKeyword, marker, pageable
				);
		}
		if (marker != null) {
			return projectRequestJpaRepository
				.findByRequestUserIdAndProjectNameContainingAndProjectRequestIdGreaterThanOrderByProjectRequestIdAsc(
					requestUserId, searchKeyword, marker, pageable
				);
		}
		return projectRequestJpaRepository
			.findByRequestUserIdAndProjectNameContainingOrderByProjectRequestIdAsc(requestUserId, searchKeyword, pageable);
	}

	@Override
	public List<ProjectRequestEntity> findByRequesterId(String requesterId, int offset, int size) {
		Pageable pageable = createPageable(offset, size);
		Page<ProjectRequestEntity> pageResult = projectRequestJpaRepository.findByRequestUserId(requesterId, pageable);
		return pageResult.getContent();
	}

	@Override
	public Optional<ProjectRequestEntity> findByRequestId(String requestId) {
		return projectRequestJpaRepository.findByProjectRequestId(requestId);
	}

	@Override
	public ProjectRequestEntity save(ProjectRequestEntity projectRequest) {
		return projectRequestJpaRepository.save(projectRequest);
	}

	@Override
	public void updateStatus(String projectId, ProjectRequestStatus status, String rejectReason) {
		projectRequestJpaRepository.updateStatusById(projectId, status, rejectReason);
	}

	@Override
	public List<ProjectRequestEntity> findAllByIds(List<String> projectRequestIds) {
		return projectRequestJpaRepository.findAllById(projectRequestIds);
	}


}
