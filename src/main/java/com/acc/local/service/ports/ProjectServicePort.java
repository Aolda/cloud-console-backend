package com.acc.local.service.ports;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.GetProjectResponse;
import com.acc.local.dto.project.InvitableUser;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;

public interface ProjectServicePort {

	List<ProjectResponse> getProjects(String keyword, String requestUserId);

	GetProjectResponse getProjectDetail(String projectId, String requesterId);

	PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String requestUserId);

	CreateProjectRequestResponse createProjectRequest(CreateProjectRequestRequest request, String requestUserId);

	List<ProjectParticipantDto> getProjectParticipants(String projectId);

	List<ProjectParticipantDto> inviteProjectParticipants(String projectId, List<String> userIds, ProjectRole projectRole, String requestUserId);

	List<ProjectParticipantDto> kickOutParticipants(String projectId, List<String> userIds, String requestUserId);

	List<InvitableUser> getInvitableUser(String projectId, String keyword, String requestUserId);

}
