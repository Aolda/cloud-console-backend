package com.acc.local.service.adapters.project;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.dto.project.CreateProjectRequest;

import com.acc.local.dto.project.CreateProjectResponse;
import com.acc.local.dto.project.ProjectListServiceDto;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.dto.project.RepositoryPagination;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.dto.project.UpdateProjectResponse;

import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.AdminProjectServicePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminProjectServiceAdapter implements AdminProjectServicePort {

	private final AuthModule authModule;
	private final ProjectModule projectModule;
	private final NeutronModule neutronModule;

	@Override
	@Transactional
	public CreateProjectResponse createProject(CreateProjectRequest createProjectRequest, String userId) {
		// TODO: userId를 통해, 요청을 보낸 사람이 Root인지 권한 확인
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(userId);
		log.info(adminToken);

		try {
			String projectOwnerId = createProjectRequest.projectOwnerId();

			KeystoneProject createdProject = projectModule.createProject(adminToken, createProjectRequest, userId);
			ProjectGlobalQuotaDto quota = applyProjectQuotaOnKeystone(adminToken, createProjectRequest, userId, createdProject);

			String createdProjectId = createdProject.getId();
			projectModule.applyProjectAccessOnOpenstack(
				createdProjectId,
				List.of(projectOwnerId), ProjectRole.PROJECT_ADMIN,
				adminToken
			);

			String projectOwnerScopedToken = authModule.issueProjectScopeToken(createdProjectId, projectOwnerId);
			neutronModule.createDefaultNetwork(projectOwnerScopedToken);

			authModule.invalidateSystemAdminToken(adminToken);
			return CreateProjectResponse.from(createdProject, quota);
		} catch(Exception e) {
			authModule.invalidateSystemAdminToken(adminToken);
			e.printStackTrace();
			throw e;
		}
	}

	private ProjectGlobalQuotaDto applyProjectQuotaOnKeystone(String adminToken, CreateProjectRequest createProjectRequest, String userId, KeystoneProject createdProject) {
		ProjectGlobalQuotaDto quota = createProjectRequest.quota();
		projectModule.updateProjectStorageQuota(adminToken, createdProject.getId(), quota.storage(), userId);
		projectModule.updateProjectCPUAndRAMQuota(adminToken, createdProject.getId(), quota.vCpu(), quota.vRam(), userId);
		return quota;
	}

	@Override
	public PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String requesterId) {
		ProjectRequestListServiceDto savedProjectRequestList = projectModule.getProjectRequestList(keyword, pageRequest);
		List<ProjectRequestDto> projectRequestsList = savedProjectRequestList.projectRequests();
		RepositoryPagination projectRequestsPagination = savedProjectRequestList.pagination();

		List<ProjectRequestResponse> projectRequestResponseList = new ArrayList<>();
		for (ProjectRequestDto projectRequest : projectRequestsList) {
			KeystoneUser requestedKeystoneUser = authModule.getUserDetail(projectRequest.requestUserId(), requesterId);
			projectRequestResponseList.add(ProjectRequestResponse.from(projectRequest, requestedKeystoneUser));
		}

		return PageResponse.<ProjectRequestResponse>builder()
			.contents(projectRequestResponseList)
			.size(projectRequestResponseList.size())
			.first(projectRequestsPagination.isFirst())
			.last(projectRequestsPagination.isLast())
			.nextMarker(projectRequestsPagination.nextMarker())
			.prevMarker(projectRequestsPagination.prevMarker())
			.build();
	}

	@Override
	@Transactional
	public void applyProjectRequestDecision(
		List<String> projectRequestIds,
		ProjectRequestStatus decision,
		String rejectReason,
		String decideUserId
	) {
		for (String projectRequestId: projectRequestIds) {
			projectModule.updateStatus(projectRequestId, decision, rejectReason);
			log.info(String.format("Project result: %s (%s) / Decided by %s", decision, projectRequestId, decideUserId));
		}
	}

	@Override
	public UpdateProjectResponse updateProject(String projectId, UpdateProjectRequest updateProjectRequest, String requesterId) {
		// TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인

		KeystoneProject updatedProject = projectModule.updateProject(projectId, updateProjectRequest, requesterId);
		return UpdateProjectResponse.from(updatedProject);
	}

	@Override
	public void deleteProject(String projectId, String requesterId) {
		// TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인
		projectModule.deleteProject(projectId, requesterId);
	}

	@Override
	public List<ProjectRole> getAssignableRoleTypes(String requesterId) {
		return Arrays.stream(ProjectRole.values()).toList();
	}

	@Override
	public PageResponse<ProjectResponse> getProjects(String keyword, PageRequest pageRequest, String requestUserId) {
		// TODO: userId를 통해, 요청을 보낸 사람이 Root인지 권한 확인
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(requestUserId);
		log.info(adminToken);

		try {
			ProjectListServiceDto projectServiceDataList = projectModule.getProjectList(keyword, pageRequest, adminToken);

			List<ProjectResponse> projectResponseList = new ArrayList<>();
			for (ProjectServiceDto projectInfo : projectServiceDataList.projects()) {
				// TODO(MR~): 사용자 통합정보 조회모듈 사용
				List<ProjectParticipantDto> projectParticipants = projectModule.getProjectParticipantList(projectInfo.projectId());

				KeystoneUser ownerKeystoneUser = null;
				if (projectInfo.ownerKeystoneId() != null) {
					ownerKeystoneUser = authModule.getUserDetail(
						projectInfo.ownerKeystoneId(),
						requestUserId
					);
				}

				projectResponseList.add(ProjectResponse.from(projectInfo, ownerKeystoneUser, projectParticipants));
			}

			authModule.invalidateSystemAdminToken(adminToken);
			return PageResponse.<ProjectResponse>builder()
				.contents(projectResponseList)
				.size(projectResponseList.size())
				.first(projectServiceDataList.pagination().isFirst())
				.last(projectServiceDataList.pagination().isLast())
				.nextMarker(projectServiceDataList.pagination().nextMarker())
				.prevMarker(projectServiceDataList.pagination().prevMarker())
				.build();
		} catch(Exception e) {
			authModule.invalidateSystemAdminToken(adminToken);
			e.printStackTrace();
			throw e;
		}
	}



}
