package com.acc.local.controller;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.AdminProjectDocs;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.project.*;
import com.acc.local.dto.auth.ProjectRoleResponse;
import com.acc.local.service.ports.AdminProjectServicePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminProjectController implements AdminProjectDocs {

	private final AdminProjectServicePort adminProjectServicePort;

	// TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
	@Override
	public ResponseEntity<CreateProjectResponse> createProject(
		Authentication authentication,
		@RequestBody CreateProjectRequest request
	) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		CreateProjectResponse response = adminProjectServicePort.createProject(request, sessionId);
		return ResponseEntity.status(201).body(response);
	}


	// TODO(MR~): 참여자 및 소유자 정보 불명확하게 담기도록 설정
	@Override
	public ResponseEntity<PageResponse<ProjectResponse>> getProjects(
		Authentication authentication,
		String keyword,
		PageRequest page
	) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		PageResponse<ProjectResponse> response = adminProjectServicePort.getProjects(keyword, page, sessionId);
		return ResponseEntity.status(200).body(response);
	}

	@Override
	public ResponseEntity<List<ProjectRoleResponse>> getProjectRoles(
		Authentication authentication
	) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		List<ProjectRole> projectRole = adminProjectServicePort.getAssignableRoleTypes(sessionId);
		List<ProjectRoleResponse> responses = projectRole.stream().map(ProjectRoleResponse::from).toList();
		return ResponseEntity.status(200).body(responses);
	}

	@Override
	public ResponseEntity<PageResponse<ProjectRequestResponse>> getProjectRequests(
		Authentication authentication,
		String keyword,
		PageRequest pageable
	) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		PageResponse<ProjectRequestResponse> response = adminProjectServicePort.getProjectRequests(keyword, pageable, sessionId);
		return ResponseEntity.status(200).body(response);
	}

	@Override
	public ResponseEntity<DecideProjectRequestResponse> decideProjectRequest(
		Authentication authentication,
		@RequestBody DecideProjectRequestRequest request
	) {
		SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
		String sessionId = principal.getSessionId();

		DecideProjectRequestResponse response = adminProjectServicePort.applyProjectRequestDecisions(
				request.projectRequestIds(),
				request.status(),
				request.reason(),
				sessionId
		);

		return ResponseEntity.status(200).body(response);
	}
}
