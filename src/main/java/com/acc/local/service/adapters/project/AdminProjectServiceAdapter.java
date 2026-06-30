package com.acc.local.service.adapters.project;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.*;

import com.acc.local.dto.project.decision.DecisionApplyInfo;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;

import com.acc.local.dto.project.quota.ProjectQuotaRequest;
import com.acc.local.dto.project.quota.QuotaGroup;
import com.acc.local.dto.project.quota.QuotaInformation;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import com.acc.local.service.modules.auth.ProjectNameValidator;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.modules.outbox.ProjectCreatedEvent;
import com.acc.local.service.modules.outbox.ProjectRequestDecisionEvent;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.AdminProjectServicePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminProjectServiceAdapter implements AdminProjectServicePort {

	private final AuthModule authModule;
	private final ProjectModule projectModule;
	private final NeutronModule neutronModule;
	private final KeystoneTokenModule keystoneTokenModule;
	private final ApplicationEventPublisher eventPublisher;
	private final SessionModule sessionModule;

	@Override
	@Transactional
	public CreateProjectResponse createProject(CreateProjectRequest createProjectRequest, String sessionId) {
		String userId = sessionModule.getKeystoneUserId(sessionId);
		ProjectNameValidator.validate(createProjectRequest.projectName());
		// TODO: userId를 통해, 요청을 보낸 사람이 Root인지 권한 확인
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(userId);

		try {
			return createProjectFromProjectCreateDto(createProjectRequest.toProjectCreateDto(), userId, adminToken);
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

		String projectOwnerScopedToken = keystoneTokenModule.issueScopedTokenByUserCredentials(projectOwnerId, createdProjectId);
		try {
			neutronModule.createDefaultNetwork(projectOwnerScopedToken);
		} finally {
			keystoneTokenModule.revokeTokenQuietly(projectOwnerScopedToken);
		}

		// 프로젝트 생성 이벤트 발행
		try {
			UserKeystoneDto ownerUser = authModule.getUserDetail(projectOwnerId, commandUserId);
			eventPublisher.publishEvent(new ProjectCreatedEvent(
					createdProjectId,
					createdProject.getName(),
					projectOwnerId,
					commandUserId,
					ownerUser.email()
			));
		} catch (Exception e) {
			log.warn("Failed to publish ProjectCreatedEvent for projectId={}", createdProjectId, e);
		}

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
	public PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String sessionId) {
		String requesterId = sessionModule.getKeystoneUserId(sessionId);
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
	public DecideProjectRequestResponse applyProjectRequestDecisions(
		List<String> projectRequestIds,
		ProjectRequestStatus decision,
		String rejectReason,
		String sessionId
	) {
		String decidedUserId = sessionModule.getKeystoneUserId(sessionId);
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(decidedUserId);

		Map<String, DecisionApplyInfo> projectRequestAppliedResults = new HashMap<>();
		int appliedCount = 0;

		List<ProjectRequestDto> projectRequestList = projectModule.getProjectRequestList(projectRequestIds);
		try {
			for (ProjectRequestDto projectRequestDto: projectRequestList) {
				String projectRequestId = projectRequestDto.projectRequestId();
				if (!projectRequestDto.status().equals(ProjectRequestStatus.PENDING)) {
					projectRequestAppliedResults.put(projectRequestId, DecisionApplyInfo.fail("이미 승인/반려여부가 결정된 프로젝트 요청입니다."));
					continue;
				}

				try {
					String createdProjectId = null;
					if (decision == ProjectRequestStatus.APPROVED) {
						CreateProjectResponse projectCreateResponse = createProjectFromProjectCreateDto(projectRequestDto.toProjectCreateDto(decidedUserId), decidedUserId, adminToken);
						createdProjectId = projectCreateResponse.projectId();
					}

					projectModule.updateStatus(projectRequestId, decision, rejectReason);
					log.info(String.format("Project result: %s (%s) / Decided by %s", decision, projectRequestId, decidedUserId));
					appliedCount++;

					projectRequestAppliedResults.put(projectRequestId, DecisionApplyInfo.success(createdProjectId));

					// 프로젝트 요청 결정 이벤트 발행
					publishProjectRequestDecisionEvent(
						projectRequestDto,
						decision,
						rejectReason,
						decidedUserId,
						createdProjectId
					);
				} catch(Exception e) {
					projectRequestAppliedResults.put(projectRequestId, DecisionApplyInfo.fail(e.getMessage()));
					log.error("Project Approve - Failed ({})", projectRequestId, e);
					revertProjectDecision(projectRequestId, decidedUserId, adminToken, e);
				}
			}

			return DecideProjectRequestResponse.builder()
					.requested(projectRequestIds.size())
					.acknowledged(projectRequestList.size())
					.applied(appliedCount)
					.data(projectRequestAppliedResults)
					.build();
		} finally {
			authModule.invalidateSystemAdminToken(adminToken);
		}
	}

	/**
	 * 프로젝트 요청 결정 이벤트 발행 (승인/거부)
	 */
	private void publishProjectRequestDecisionEvent(
		ProjectRequestDto projectRequest,
		ProjectRequestStatus decision,
		String rejectReason,
		String decidedUserId,
		String createdProjectId
	) {
		try {
			// 요청자 정보 조회
			UserKeystoneDto requesterUser = authModule.getUserDetail(
				projectRequest.requestUserId(),
				decidedUserId
			);

			ProjectRequestDecisionEvent event = new ProjectRequestDecisionEvent(
				projectRequest.projectRequestId(),
				projectRequest.requestUserId(),
				projectRequest.projectName(),
				projectRequest.description(),
				decision,
				rejectReason,
				createdProjectId,
				requesterUser.email()
			);

			eventPublisher.publishEvent(event);
		} catch (Exception e) {
			log.warn("Failed to publish ProjectRequestDecisionEvent: projectRequestId={}", projectRequest.projectRequestId(), e);
		}
	}

    private void revertProjectDecision(
			String projectRequestId,
			String approvedUserId,
			String adminToken,
			Exception originalException
	) {
			try {
				ProjectRequestDto projectRequest = projectModule.getProjectRequest(projectRequestId);

				ProjectListServiceDto searchedProjectList = projectModule.getProjectList(projectRequest.projectName(), null, adminToken);
				for (ProjectServiceDto projectDto: searchedProjectList.projects()) {
					if (!Objects.equals(
							projectDto.description(),
							ProjectRequestDto.getProjectDescriptionMessage(projectRequest.projectRequestId(), approvedUserId)
					)) {
						continue;
					}

					String projectId = projectDto.projectId();
					projectModule.deleteProjectWithToken(projectId, adminToken);
				}

				if (!projectRequest.status().equals(ProjectRequestStatus.PENDING)) {
					projectModule.revertStatus(projectRequestId);
				}

			} catch (Exception e) {
				e.addSuppressed(originalException);
				throw new AuthServiceException(
						AuthErrorCode.KEYSTONE_API_FAILURE,
						"Openstack 프로젝트 정합성 복구에 실패했습니다",
						e
				);
			}
    }

	@Override
	public UpdateProjectResponse updateProject(String projectId, UpdateProjectRequest updateProjectRequest, String sessionId) {
		String requesterId = sessionModule.getKeystoneUserId(sessionId);
		// TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인

		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(requesterId);
		try {
			KeystoneProject updatedProject = projectModule.updateProjectWithToken(projectId, updateProjectRequest, adminToken);
			return UpdateProjectResponse.from(updatedProject);
		} finally {
			authModule.invalidateSystemAdminToken(adminToken);
		}
	}

	@Override
	public void deleteProject(String projectId, String sessionId) {
		String requesterId = sessionModule.getKeystoneUserId(sessionId);
		// TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(requesterId);
		try {
			projectModule.deleteProjectWithToken(projectId, adminToken);
		} finally {
			authModule.invalidateSystemAdminToken(adminToken);
		}
	}

	@Override
	public List<ProjectRole> getAssignableRoleTypes() {
		return Arrays.stream(ProjectRole.values()).toList();
	}

	@Override
	public PageResponse<ProjectResponse> getProjects(String keyword, PageRequest pageRequest, String sessionId) {
		String requestUserId = sessionModule.getKeystoneUserId(sessionId);
		// TODO: userId를 통해, 요청을 보낸 사람이 Root인지 권한 확인
		String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope(requestUserId);

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
