package com.acc.local.controller;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.AdminProjectDocs;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.CreateProjectResponse;
import com.acc.local.dto.project.DecideProjectRequestRequest;
import com.acc.local.dto.auth.ProjectRoleResponse;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;
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
		JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
		String userId = jwtInfo.getUserId();

		CreateProjectResponse response = adminProjectServicePort.createProject(request, userId);
		return ResponseEntity.status(201).body(response);
	}


	// TODO(MR~): 참여자 및 소유자 정보 불명확하게 담기도록 설정
	@Override
	public ResponseEntity<PageResponse<ProjectResponse>> getProjects(
		Authentication authentication,
		String keyword,
		PageRequest page
	) {
		JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
		String userId = jwtInfo.getUserId();

		PageResponse<ProjectResponse> response = adminProjectServicePort.getProjects(keyword, page, userId);
		return ResponseEntity.status(200).body(response);
	}

	@Override
	public ResponseEntity<List<ProjectRoleResponse>> getProjectRoles(
		Authentication authentication
	) {
		JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
		String userId = jwtInfo.getUserId();

		List<ProjectRole> projectRole = adminProjectServicePort.getAssignableRoleTypes(userId);
		List<ProjectRoleResponse> responses = projectRole.stream().map(ProjectRoleResponse::from).toList();
		return ResponseEntity.status(200).body(responses);
	}

	@Override
	public ResponseEntity<PageResponse<ProjectRequestResponse>> getProjectRequests(
		Authentication authentication,
		String keyword,
		PageRequest pageable
	) {
		JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
		String userId = jwtInfo.getUserId();

		PageResponse<ProjectRequestResponse> response = adminProjectServicePort.getProjectRequests(keyword, pageable, userId);
		return ResponseEntity.status(201).body(response);
	}

	@Override
	public ResponseEntity<Void> decideProjectRequest(
		Authentication authentication,
		@RequestBody DecideProjectRequestRequest request
	) {
		JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
		String userId = jwtInfo.getUserId();

		adminProjectServicePort.applyProjectRequestDecision(
			request.projectRequestIds(),
			request.status(),
			request.reason(),
			userId
		);

		return ResponseEntity.status(200).build();
	}
}
