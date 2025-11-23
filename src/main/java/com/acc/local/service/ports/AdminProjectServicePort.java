package com.acc.local.service.ports;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.CreateProjectResponse;
import com.acc.local.dto.project.GetProjectResponse;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.dto.project.UpdateProjectResponse;

public interface AdminProjectServicePort {

	CreateProjectRequestResponse createProjectRequest(CreateProjectRequestRequest request, String requestUserId);

	PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String requestUserId);

	void applyProjectRequestDecision(List<String> projectRequestIds, ProjectRequestStatus decision, String rejectReason, String decideUserId);

	CreateProjectResponse createProject(CreateProjectRequest createProjectRequest, String userId);

	GetProjectResponse getProjectDetail(String projectId, String requesterId);

	UpdateProjectResponse updateProject(String projectId, UpdateProjectRequest updateProjectRequest, String requesterId);

	void deleteProject(String projectId, String requesterId);

	List<ProjectRole> getAssignableRoleTypes(String requesterId);

	PageResponse<ProjectResponse> getProjects(String keyword, PageRequest pageRequest, String requestUserId);
}
