package com.acc.local.service.adapters.project;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.project.ProjectErrorCode;
import com.acc.global.exception.project.ProjectServiceException;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.GetProjectResponse;
import com.acc.local.dto.project.InvitableUser;
import com.acc.local.dto.project.ProjectListServiceDto;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.dto.project.RepositoryPagination;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.ports.ProjectServicePort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceAdapter implements ProjectServicePort {

	private final ProjectModule projectModule;
	private final AuthModule authModule;
	private final UserModule userModule;

	@Override
	public List<ProjectResponse> getProjects(String keyword, String requestUserId) {
		// TODO: userId를 통해, 요청을 보낸 사람이 Root인지 권한 확인
		String unscopedToken = authModule.getUnscopedTokenByUserId(requestUserId);
		String adminTokenWithProject = authModule.issueSystemAdminTokenWithAdminProjectScope(requestUserId);

		try {
			List<ProjectServiceDto> projectServiceDataList = projectModule.getAllProjectListForUser(keyword, requestUserId, unscopedToken, adminTokenWithProject);
			authModule.invalidateSystemAdminToken(adminTokenWithProject);

			List<ProjectResponse> projectResponseList = new ArrayList<>();
			for (ProjectServiceDto projectInfo : projectServiceDataList) {
				// TODO(MR~): 사용자 통합정보 조회모듈 사용
				List<ProjectParticipantDto> projectParticipants = projectModule.getProjectParticipantList(projectInfo.projectId());

				KeystoneUser ownerUser = null;
				if (projectInfo.ownerKeystoneId() != null) {
					ownerUser = authModule.getUserDetail(
						projectInfo.ownerKeystoneId(),
						requestUserId
					);
				}

				projectResponseList.add(ProjectResponse.from(projectInfo, ownerUser, projectParticipants));
			}

			KeystoneUser userDetail = authModule.getUserDetail(requestUserId, requestUserId);
			projectModule.getAllProjectRequestList(keyword, requestUserId).stream()
				.filter(v -> v.status() != ProjectRequestStatus.APPROVED)
				.forEach(
					v -> projectResponseList.add(ProjectResponse.from(v, userDetail))
				);

			return projectResponseList;
		} catch(Exception e) {
			authModule.invalidateSystemAdminToken(adminTokenWithProject);
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public GetProjectResponse getProjectDetail(String projectId, String requestUserId) {
		// TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인
		String scopedToken = authModule.issueProjectScopeToken(projectId, requestUserId);
		ProjectServiceDto project = projectModule.getProjectDetail(projectId, scopedToken);
		return GetProjectResponse.from(project);
	}

	@Override
	public PageResponse<ProjectRequestResponse> getProjectRequests(String keyword, PageRequest pageRequest, String requesterId) {
		ProjectRequestListServiceDto savedProjectRequestList = projectModule.getProjectRequestList(keyword, pageRequest);
		List<ProjectRequestDto> projectRequestsList = savedProjectRequestList.projectRequests();
		RepositoryPagination projectRequestsPagination = savedProjectRequestList.pagination();

		List<ProjectRequestResponse> projectRequestResponseList = new ArrayList<>();
		for (ProjectRequestDto projectRequest : projectRequestsList) {
			KeystoneUser requestedUser = authModule.getUserDetail(projectRequest.requestUserId(), requesterId);
			projectRequestResponseList.add(ProjectRequestResponse.from(projectRequest, requestedUser));
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
	public CreateProjectRequestResponse createProjectRequest(CreateProjectRequestRequest request, String requesterId) {
		return projectModule.createProjectRequest(request, requesterId);
	}

	@Override
	public List<ProjectParticipantDto> getProjectParticipants(String projectId) {
		try {
			return projectModule.getProjectParticipantList(projectId);
		} catch (Exception e) {
			if (e instanceof ProjectServiceException) throw e;
			if (e instanceof AccBaseException) throw e;
			throw new ProjectServiceException(ProjectErrorCode.UNEXPECTED_ERROR, e.getMessage());
		}
	}

	@Override
	@Transactional
	public List<ProjectParticipantDto> inviteProjectParticipants(String projectId, List<String> userIds, ProjectRole projectRole, String requestUserId) {
		String token = authModule.issueProjectScopeToken(requestUserId, projectId);
		try {
			projectModule.checkUserPrivilege(projectId, requestUserId);

			if(projectModule.isParticipateUserExists(projectId, userIds)) {
				throw new ProjectServiceException(ProjectErrorCode.CONFLICT, "이미 초대되어있는 사용자가 요청에 포함되어있습니다.");
			}

			projectModule.inviteParticipants(projectId, userIds, projectRole, token);

			authModule.invalidateServiceTokensByUserId(requestUserId);
			return null;
		} catch (Exception e) {
			authModule.invalidateServiceTokensByUserId(requestUserId);
			if (e instanceof AccBaseException) throw e;
			throw new ProjectServiceException(ProjectErrorCode.UNEXPECTED_ERROR, e.getMessage());
		}
	}

	@Override
	public List<ProjectParticipantDto> kickOutParticipants(String projectId, List<String> userIds, String requestUserId) {
		String token = authModule.issueProjectScopeToken(requestUserId, projectId);
		try {
			projectModule.checkUserPrivilege(projectId, requestUserId);

			if (userIds.contains(requestUserId)) {
				throw new ProjectServiceException(ProjectErrorCode.INVALID_REQUEST_PARAMETER, "프로젝트 관리자의 프로젝트 접근권한은 삭제할 수 없습니다.");
			}

			if(projectModule.isNotParticipateUserExists(projectId, userIds)) {
				throw new ProjectServiceException(ProjectErrorCode.INVALID_REQUEST_PARAMETER, "초대되지 않은 사용자가 요청에 포함되어있습니다.");
			}

			projectModule.kickOutParticipants(projectId, userIds, token);

			authModule.invalidateServiceTokensByUserId(requestUserId);
			return null;
		} catch (Exception e) {
			authModule.invalidateServiceTokensByUserId(requestUserId);
			if (e instanceof AccBaseException) throw e;
			throw new ProjectServiceException(ProjectErrorCode.UNEXPECTED_ERROR, e.getMessage());
		}
	}

	@Override
	public List<InvitableUser> getInvitableUser(String projectId, String keyword, String requestUserId) {
		try {
			String token = authModule.issueSystemAdminToken(requestUserId);
			List<InvitableUser> invitableUserList = projectModule.getInvitableUserList(projectId, keyword, token);
			authModule.invalidateSystemAdminToken(token);

			return invitableUserList;
		} catch (Exception e) {
				if (e instanceof ProjectServiceException) throw e;
				if (e instanceof AccBaseException) throw e;
				throw new ProjectServiceException(ProjectErrorCode.UNEXPECTED_ERROR, e.getMessage());
		}
	}
}
