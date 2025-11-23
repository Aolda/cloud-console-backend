package com.acc.local.repository.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.entity.ProjectRequestEntity;
import java.util.List;
import java.util.Optional;

public interface ProjectRequestRepositoryPort {

	List<ProjectRequestEntity> findAllByKeyword(String keyword, int offset, int size);

	List<ProjectRequestEntity> findByRequesterId(String requesterId, int offset, int size);

	List<ProjectRequestEntity> findByRequestId(String requestId, int offset, int size);

	ProjectRequestEntity save(ProjectRequestEntity projectRequest);

	void updateStatus(String projectId, ProjectRequestStatus status, String rejectReason);
}
