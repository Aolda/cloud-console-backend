package com.acc.local.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.ProjectDocs;
import com.acc.local.dto.project.InvitableUser;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.GetProjectResponse;
import com.acc.local.dto.project.InviteProjectRequest;
import com.acc.local.dto.project.KickOutProjectRequest;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.service.ports.ProjectServicePort;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectDocs {

	private final ProjectServicePort projectServicePort;

	@Override
	public ResponseEntity<List<ProjectResponse>> getProjects(Authentication authentication, String keyword) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		List<ProjectResponse> projects = projectServicePort.getProjects(keyword, sessionId);
		return ResponseEntity.status(200).body(projects);
	}

	@Override
	public ResponseEntity<GetProjectResponse> getProjectSpec(Authentication authentication, String projectId) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		GetProjectResponse response = projectServicePort.getProjectDetail(projectId, sessionId);
		return ResponseEntity.status(200).body(response);
	}

	@Override
	public ResponseEntity<PageResponse<ProjectRequestResponse>> getProjectRequests(Authentication authentication, String keyword, PageRequest pageable) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		PageResponse<ProjectRequestResponse> response = projectServicePort.getProjectRequests(keyword, pageable, sessionId);
		return ResponseEntity.status(200).body(response);
	}

	@Override
	public ResponseEntity<CreateProjectRequestResponse> createProjectRequest(Authentication authentication, CreateProjectRequestRequest createProjectRequestRequest) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		CreateProjectRequestResponse response = projectServicePort.createProjectRequest(createProjectRequestRequest, sessionId);
		return ResponseEntity.status(201).body(response);
	}

	@Override
	public ResponseEntity<List<ProjectParticipantDto>> inviteProjectParticipants(Authentication authentication, InviteProjectRequest inviteProjectRequest, String projectId) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		List<ProjectParticipantDto> updatedParticipantList = projectServicePort.inviteProjectParticipants(
			projectId,
			inviteProjectRequest.userIds(),
			inviteProjectRequest.role(),
			sessionId
		);
		return ResponseEntity.status(200).body(updatedParticipantList);
	}

	@Override
	public ResponseEntity<List<ProjectParticipantDto>> kickOutProjectParticipants(Authentication authentication, KickOutProjectRequest inviteProjectRequest, String projectId) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		List<ProjectParticipantDto> updatedParticipantList = projectServicePort.kickOutParticipants(
			projectId,
			inviteProjectRequest.userIds(),
			sessionId
		);
		return ResponseEntity.status(200).body(updatedParticipantList);
	}

	@Override
	public ResponseEntity<List<InvitableUser>> getInvitableParticipants(Authentication authentication, String keyword, String projectId) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		List<InvitableUser> invitableUser = projectServicePort.getInvitableUser(projectId, keyword, sessionId);
		return ResponseEntity.status(200).body(invitableUser);
	}

}
