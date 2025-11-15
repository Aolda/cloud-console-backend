package com.acc.local.controller;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.AdminProjectDocs;
import com.acc.local.dto.project.CreateProjectCreationResponse;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.DecideProjectCreationRequest;
import com.acc.local.dto.auth.ProjectRoleResponse;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.service.ports.AuthServicePort;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminProjectController implements AdminProjectDocs {

	private final AuthServicePort authServicePort;

	@Override
	public ResponseEntity<PageResponse<ProjectResponse>> getProjects(
		Authentication authentication,
		PageRequest page
	) {

		return null;
	}

	@Override
	public ResponseEntity<ProjectResponse> createProject(
		Authentication authentication,
		CreateProjectRequest request
	) {
		return null;
	}

	@Override
	public ResponseEntity<List<ProjectRoleResponse>> getProjectRoles(
		Authentication authentication
	) {
		return null;
	}

	@Override
	public ResponseEntity<PageResponse<CreateProjectCreationResponse>> getProjectCreationRequests(
		Authentication authentication,
		PageRequest pageable
	) {
		return null;
	}

	@Override
	public ResponseEntity<Void> handleProjectCreationRequest(
		Authentication authentication,
		DecideProjectCreationRequest request
	) {
		return null;
	}
}
