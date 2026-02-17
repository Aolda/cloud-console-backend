package com.acc.local.service.adapters.project;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.*;

import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;

import com.acc.local.dto.project.quota.ProjectQuotaRequest;
import com.acc.local.dto.project.quota.QuotaGroup;
import com.acc.local.dto.project.quota.QuotaInformation;
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
			return createProjectFromProjectCreateDto(createProjectRequest.toProjectCreateDto(), userId, adminToken);
		} catch(Exception e) {
			authModule.invalidateSystemAdminToken(adminToken);
			e.printStackTrace();
			throw e;
		} finally {
			authModule.invalidateSystemAdminToken(adminToken);
		}
	}

	private CreateProjectResponse createProjectFromProjectCreateDto(ProjectCreateDto projectCreateDto, String commandUserId, String adminToken) {
		String projectOwnerId = projectCreateDto.projectOwnerId();

		KeystoneProject createdProject = projectModule.createProject(adminToken, projectCreateDto, commandUserId);
		ProjectGlobalQuotaDto quota = applyProjectQuotaOnKeystone(adminToken, projectCreateDto, commandUserId, createdProject);

		String createdProjectId = createdProject.getId();
		projectModule.applyProjectAccessOnOpenstack(
			createdProjectId,
			List.of(projectOwnerId), ProjectRole.PROJECT_ADMIN,
				adminToken
		);

		String projectOwnerScopedToken = authModule.issueProjectScopeToken(createdProjectId, projectOwnerId);
		neutronModule.createDefaultNetwork(projectOwnerScopedToken);

		return CreateProjectResponse.from(createdProject, quota);
	}

	private ProjectGlobalQuotaDto applyProjectQuotaOnKeystone(String adminToken, ProjectCreateDto projectCreateDto, String userId, KeystoneProject createdProject) {
		ProjectQuotaRequest quota = projectCreateDto.quota();
		projectModule.updateProjectStorageQuota(adminToken, createdProject.getId(), quota.storage(), userId);
		projectModule.updateProjectCPUAndRAMQuota(adminToken, createdProject.getId(), quota.vCpu(), quota.vRam(), userId);
		return ProjectGlobalQuotaDto.builder()
			.instance(QuotaInformation.builder()
				.available(quota.instance())
				.build())
			.core(QuotaInformation.builder()
				.available(quota.vCpu())
				.build())
			.ram(QuotaInformation.builder()
				.available(quota.vRam())
				.build())
			.volume(QuotaGroup.builder()
				.size(QuotaInformation.builder()
					.available(quota.storage())
					.build())
				.build())
			.build();
	}

	@Override
	public PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String requesterId) {
		ProjectRequestListServiceDto savedProjectRequestList = projectModule.getProjectRequestList(keyword, pageRequest);
		List<ProjectRequestDto> projectRequestsList = savedProjectRequestList.projectRequests();
		RepositoryPagination projectRequestsPagination = savedProjectRequestList.pagination();

		List<ProjectRequestResponse> projectRequestResponseList = new ArrayList<>();
		for (ProjectRequestDto projectRequest : projectRequestsList) {
			UserKeystoneDto requestedUserKeystoneDto = authModule.getUserDetail(projectRequest.requestUserId(), requesterId);
			projectRequestResponseList.add(ProjectRequestResponse.from(projectRequest, requestedUserKeystoneDto));
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
	public List<String> applyProjectRequestDecisions(
		List<String> projectRequestIds,
		ProjectRequestStatus decision,
		String rejectReason,
		String decidedUserId
	) {
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(decidedUserId);

		List<String> succeedProjectRequestIds = new ArrayList<>();
		List<ProjectRequestDto> projectRequestList = projectModule.getProjectRequestList(projectRequestIds);
		for (ProjectRequestDto projectRequestDto: projectRequestList) {
			String projectRequestId = projectRequestDto.projectRequestId();
			try {
				if (decision == ProjectRequestStatus.APPROVED) {
					createProjectFromProjectCreateDto(projectRequestDto.toProjectCreateDto(decidedUserId), decidedUserId, adminToken);
				}

				projectModule.updateStatus(projectRequestId, decision, rejectReason);
				log.info(String.format("Project result: %s (%s) / Decided by %s", decision, projectRequestId, decidedUserId));

				succeedProjectRequestIds.add(projectRequestId);
			} catch(Exception e) {
				log.error("Project Approve - Failed ({})", projectRequestId);
				revertProjectDecision(projectRequestId, decidedUserId);
				e.printStackTrace();
			}
		}
		authModule.invalidateSystemAdminToken(adminToken);
		return succeedProjectRequestIds;
	}

    private void revertProjectDecision(String projectRequestId, String approvedUserId) {
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(approvedUserId);
		try {
			ProjectRequestDto projectRequest = projectModule.getProjectRequest(projectRequestId);

			ProjectListServiceDto searchedProjectList = projectModule.getProjectList(projectRequest.projectName(), null, adminToken);
			for (ProjectServiceDto projectDto: searchedProjectList.projects()) {
				if (!projectDto.description().equals(
						ProjectRequestDto.getProjectDescriptionMessage(projectRequest.projectRequestId(), approvedUserId)
				)) {
					continue;
				}

				String projectId = projectDto.projectId();
				projectModule.deleteProject(projectId, approvedUserId);
			}

			if (!projectRequest.status().equals(ProjectRequestStatus.PENDING)) {
				projectModule.revertStatus(projectRequestId);
			}

		} catch (Exception e) {
			throw new AuthServiceException(AuthErrorCode.KEYSTONE_API_FAILURE, "Openstack 프로젝트 정합성 복구에 실패했습니다");
		} finally {
			authModule.invalidateSystemAdminToken(adminToken);
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

				UserKeystoneDto ownerUserKeystoneDto = null;
				if (projectInfo.ownerKeystoneId() != null) {
					ownerUserKeystoneDto = authModule.getUserDetail(
						projectInfo.ownerKeystoneId(),
						requestUserId
					);
				}

				projectResponseList.add(ProjectResponse.from(projectInfo, ownerUserKeystoneDto, projectParticipants));
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
