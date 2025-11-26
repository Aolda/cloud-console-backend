package com.acc.local.repository.adapters;

import com.acc.global.common.PageResponse;
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

@Repository
@RequiredArgsConstructor
public class ProjectRequestRepositoryAdapter implements ProjectRequestRepositoryPort {

	private final ProjectRequestJpaRepository projectRequestJpaRepository;

	private Pageable createPageable(int offset, int size) {
		// pageNumber는 0부터 시작합니다.
		int pageNumber = offset / size;
		return PageRequest.of(pageNumber, size);
	}


	@Override
	public List<ProjectRequestEntity> findAllByKeyword(String keyword) {
		return projectRequestJpaRepository.findByProjectNameContaining(keyword);
	}

	@Override
	public List<ProjectRequestEntity> findAllByKeyword(String keyword, int offset, int size) {
		return projectRequestJpaRepository.findByProjectNameContaining(keyword, createPageable(offset, size)).getContent();
	}

	@Override
	public List<ProjectRequestEntity> findByRequesterId(String requesterId, int offset, int size) {
		Pageable pageable = createPageable(offset, size);
		Page<ProjectRequestEntity> pageResult = projectRequestJpaRepository.findByRequestUserId(requesterId, pageable);
		return pageResult.getContent();
	}

	@Override
	public List<ProjectRequestEntity> findByRequestId(String requestId, int offset, int size) {
		Pageable pageable = createPageable(offset, size);
		Page<ProjectRequestEntity> pageResult = projectRequestJpaRepository.findByProjectRequestId(requestId, pageable);
		return pageResult.getContent();
	}

	@Override
	public ProjectRequestEntity save(ProjectRequestEntity projectRequest) {
		return projectRequestJpaRepository.save(projectRequest);
	}

	@Override
	public void updateStatus(String projectId, ProjectRequestStatus status, String rejectReason) {
		projectRequestJpaRepository.updateStatusById(projectId, status, rejectReason);
	}


}
