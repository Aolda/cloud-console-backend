package com.acc.local.service.ports;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.project.*;

public interface AdminProjectServicePort {

	PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String sessionId);

	DecideProjectRequestResponse applyProjectRequestDecisions(List<String> projectRequestIds, ProjectRequestStatus decision, String rejectReason, String sessionId);

	CreateProjectResponse createProject(CreateProjectRequest createProjectRequest, String sessionId);

	UpdateProjectResponse updateProject(String projectId, UpdateProjectRequest updateProjectRequest, String sessionId);

	void deleteProject(String projectId, String sessionId);

	List<ProjectRole> getAssignableRoleTypes(String sessionId);

	PageResponse<ProjectResponse> getProjects(String keyword, PageRequest pageRequest, String sessionId);
}
