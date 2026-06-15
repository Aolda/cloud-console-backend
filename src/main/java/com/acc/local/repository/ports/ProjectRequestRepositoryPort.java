package com.acc.local.repository.ports;

import com.acc.global.common.PageRequest;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.entity.ProjectRequestEntity;

import java.util.List;
import java.util.Optional;

public interface ProjectRequestRepositoryPort {

	List<ProjectRequestEntity> findAllByKeyword(String keyword, String requestUserId);

	List<ProjectRequestEntity> findAllByKeyword(String keyword, String marker, PageRequest.Direction direction, int size);

	List<ProjectRequestEntity> findByRequesterId(String requesterId, int offset, int size);

	Optional<ProjectRequestEntity> findByRequestId(String requestId);

	ProjectRequestEntity save(ProjectRequestEntity projectRequest);

	void updateStatus(String projectId, ProjectRequestStatus status, String rejectReason);

	List<ProjectRequestEntity> findAllByIds(List<String> projectRequestIds);

	List<ProjectRequestEntity> findAllByKeywordAndRequestUserId(
		String searchKeyword,
		String requestUserId,
		String marker,
		PageRequest.Direction direction,
		int size
	);
}
