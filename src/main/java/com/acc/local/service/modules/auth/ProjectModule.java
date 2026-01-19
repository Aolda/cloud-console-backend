package com.acc.local.service.modules.auth;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.dto.keystone.UpdateKeystoneProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.acc.global.common.PageRequest;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.exception.auth.KeystoneException;
import com.acc.global.exception.project.ProjectErrorCode;
import com.acc.global.exception.project.ProjectServiceException;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.InvitableUser;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.ProjectListServiceDto;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.dto.project.quota.ProjectComputeQuotaDto;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.dto.project.quota.ProjectStorageQuotaDto;
import com.acc.local.dto.project.quota.QuotaInformation;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.ProjectParticipantEntity;
import com.acc.local.entity.ProjectRequestEntity;
import com.acc.local.entity.id.ProjectParticipantId;
import com.acc.local.external.dto.keystone.CreateKeystoneProjectRequest;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.external.modules.keystone.KeystoneUserAPIModule;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.external.ports.VolumeQuotaExternalPort;
import com.acc.local.external.ports.compute.ComputeQuotaExternalPort;
import com.acc.local.repository.ports.ProjectParticipantRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import com.acc.local.repository.ports.ProjectRequestRepositoryPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectModule {

	private final AuthModule authModule;

	private final VolumeQuotaExternalPort volumeQuotaExternalPort;
	private final ComputeQuotaExternalPort computeQuotaExternalPort;
	private final KeystoneAPIExternalPort keystoneAPIExternalPort;

	private final UserRepositoryPort userRepositoryPort;
	private final ProjectRepositoryPort projectRepositoryPort;
	private final ProjectRequestRepositoryPort projectRequestRepositoryPort;
	private final ProjectParticipantRepositoryPort projectParticipantRepositoryPort;
	private final KeystoneUserAPIModule keystoneUserAPIModule;

	// ============ Project Request ============
	public CreateProjectRequestResponse createProjectRequest(CreateProjectRequestRequest request, String requestUserId) {
		ProjectRequestEntity newRequest = ProjectRequestEntity.builder()
			.projectRequestId(UUID.randomUUID().toString())
			.requestUserId(requestUserId)
			.projectName(request.projectName())
			.projectType(request.projectType())
			.projectDescription(request.projectDescription())
			.status(ProjectRequestStatus.PENDING)
			.build();

		ProjectRequestEntity savedEntity = projectRequestRepositoryPort.save(newRequest);
		return CreateProjectRequestResponse.from(savedEntity);
	}

	public ProjectRequestListServiceDto getProjectRequestList(String keyword, PageRequest pageRequest) {
		String searchKeyword = (keyword == null) ? "" : keyword;

		String marker = "";
		int offset = 0;
		int size = 10;
		if (pageRequest != null) {
			pageRequest.getMarker();
			offset = getOffsetFromMarker(marker);
			size = pageRequest.getLimit();
		}

		List<ProjectRequestEntity> savedProjectRequestList = projectRequestRepositoryPort.findAllByKeyword(
			searchKeyword,
			offset, size
		);

		return ProjectRequestListServiceDto.from(
			savedProjectRequestList.stream().map(ProjectRequestDto::from).toList(),
			pageRequest, false, null
		);
	}

	public List<ProjectRequestDto> getAllProjectRequestList(String keyword, String requestUserId) {
		String searchKeyword = (keyword == null) ? "" : keyword;

		List<ProjectRequestEntity> savedProjectRequestList = projectRequestRepositoryPort.findAllByKeyword(searchKeyword, requestUserId);

		return savedProjectRequestList.stream().map(ProjectRequestDto::from).toList();
	}

	private static int getOffsetFromMarker(String marker) {
		try {
			Base64.Decoder decoder = Base64.getDecoder();
			return Integer.parseInt(new String(decoder.decode(marker), StandardCharsets.UTF_8));
		} catch (Exception e) {
			return 0;
		}
	}

	public void updateStatus(String projectRequestId, ProjectRequestStatus decision, String rejectReason) {
		if (decision != ProjectRequestStatus.APPROVED && decision != ProjectRequestStatus.REJECTED) {
			throw new IllegalArgumentException("결정 상태는 APPROVED 또는 REJECTED여야 합니다.");
		}

		if (decision == ProjectRequestStatus.REJECTED && (rejectReason == null || rejectReason.isBlank())) {
			throw new IllegalArgumentException("거절 사유는 필수입니다.");
		}

		projectRequestRepositoryPort.updateStatus(projectRequestId, decision, rejectReason);
	}

	// ============ Project ============
	public ProjectListServiceDto getProjectList(String keyword, PageRequest pageRequest, String adminToken) {
		ProjectListDto openstackProjectResponse = keystoneAPIExternalPort.getProjectsByProjectName(
			keyword,
			pageRequest, adminToken
		);

		List<ProjectServiceDto> responseList = new ArrayList<>();
		for (KeystoneProject openstackProject: openstackProjectResponse.projectList()) {
			try {
				String projectId = openstackProject.getId();
				ProjectEntity databaseProject = getDatabaseProject(projectId);
				ProjectComputeQuotaDto projectComputeQuotaDetail = getProjectComputeQuotaDetail(projectId, adminToken);
				ProjectStorageQuotaDto projectStorageQuotaDetail = getProjectStorageQuotaDetail(projectId, adminToken);
				responseList.add(ProjectServiceDto.from(
					databaseProject, openstackProject,
					projectComputeQuotaDetail, projectStorageQuotaDetail
				));
			} catch (AuthServiceException e) {
				throw e;
			}
		}

		return ProjectListServiceDto.builder()
			.pagination(openstackProjectResponse.pageInfo())
			.projects(responseList)
			.build();
	}

	public ProjectListServiceDto getProjectListForUser(String keyword, PageRequest pageRequest, String requestUserId, String adminToken) {
		ProjectListDto openstackProjectResponse = keystoneAPIExternalPort.getUserProjectsByProjectName(
			keyword,
			pageRequest, requestUserId,
			adminToken
		);

		List<ProjectServiceDto> responseList = new ArrayList<>();
		for (KeystoneProject openstackProject: openstackProjectResponse.projectList()) {
			String projectId = openstackProject.getId();
			ProjectEntity databaseProject = getDatabaseProject(projectId);
			ProjectComputeQuotaDto projectComputeQuotaDetail = getProjectComputeQuotaDetail(projectId, adminToken);
			ProjectStorageQuotaDto projectStorageQuotaDetail = getProjectStorageQuotaDetail(projectId, adminToken);
			responseList.add(ProjectServiceDto.from(
				databaseProject, openstackProject,
				projectComputeQuotaDetail, projectStorageQuotaDetail
			));
		}

		return ProjectListServiceDto.builder()
			.pagination(openstackProjectResponse.pageInfo())
			.projects(responseList)
			.build();
	}

	public List<ProjectServiceDto> getAllProjectListForUser(String keyword, String requestUserId, String requestUserUnscopeToken, String adminTokenWithProject) {
		ProjectListDto openstackProjectResponse = keystoneAPIExternalPort.getUserProjectsByProjectName(
			keyword,
			null, requestUserId,
			requestUserUnscopeToken
		);

		List<ProjectServiceDto> responseList = new ArrayList<>();
		for (KeystoneProject openstackProject: openstackProjectResponse.projectList()) {
			try {
				String projectId = openstackProject.getId();
				ProjectEntity databaseProject = getDatabaseProject(projectId);
				ProjectComputeQuotaDto projectComputeQuota = getProjectComputeQuotaDetail(projectId, adminTokenWithProject);
				ProjectStorageQuotaDto projectStorageQuotaDetail = getProjectStorageQuotaDetail(projectId, adminTokenWithProject);
				responseList.add(ProjectServiceDto.from(
					databaseProject, openstackProject,
					projectComputeQuota, projectStorageQuotaDetail
				));
			} catch (AuthServiceException e) {
				e.printStackTrace();
				throw e;
			}
		}

		return responseList;
	}

	private ProjectEntity getDatabaseProject(String projectId) {
		Optional<ProjectEntity> databaseProjectOrNull = projectRepositoryPort.findById(projectId);

		if (databaseProjectOrNull.isEmpty()) {
			throw new AuthServiceException(AuthErrorCode.PROJECT_NOT_FOUND, projectId);
		}

		return databaseProjectOrNull.get();
	}

	public KeystoneProject createProject(String adminToken, CreateProjectRequest request, String commandUserId) {
		CreateKeystoneProjectRequest project = CreateKeystoneProjectRequest.builder()
			.projectName(request.projectName())
			.projectDescription(request.projectDescription())
			.build();
		KeystoneProject keystoneSavedProject = keystoneAPIExternalPort.createProject(adminToken, project);

		ProjectEntity aoldaProject = ProjectEntity.builder()
			.projectId(keystoneSavedProject.getId())
			.ownerKeystoneId(request.projectOwnerId())
			.quotaVCpuCount((long)request.quota().vCpu())
			.quotaVRamMB((long)request.quota().vRam())
			.quotaStorageGB((long)request.quota().storage())
			.quotaInstanceCount((long)request.quota().instance())
			// .projectType()
			.build();
		projectRepositoryPort.save(aoldaProject);

		UserDbExtraEntity projectOwnerUserDetail = userRepositoryPort.findUserDetailById(request.projectOwnerId())
			.orElseThrow(() -> new ProjectServiceException(ProjectErrorCode.USER_NOT_FOUND));

		ProjectParticipantId participantId = new ProjectParticipantId(
			aoldaProject.getProjectId(),  // ProjectEntity의 ID 값
			projectOwnerUserDetail.getUserId()   // UserDetailEntity의 ID 값
		);
		ProjectParticipantEntity mappedOwnerRole = ProjectParticipantEntity.builder()
			.projectParticipantId(participantId)
			.userDetail(projectOwnerUserDetail)
			.project(aoldaProject)
			.role(ProjectRole.PROJECT_ADMIN)
			.build();
		projectParticipantRepositoryPort.save(mappedOwnerRole);

		aoldaProject.setParticipants(List.of(mappedOwnerRole));

		return keystoneSavedProject;
	}

	@Transactional
	public ProjectServiceDto getProjectDetail(String projectId, String scopedToken) {
		KeystoneProject openstackProject = keystoneAPIExternalPort.getProjectDetail(projectId, scopedToken);
		ProjectEntity databaseProject = getDatabaseProject(projectId);

		ProjectComputeQuotaDto projectComputeQuotaDetail = getProjectComputeQuotaDetail(projectId, scopedToken);
		ProjectStorageQuotaDto projectStorageQuotaDetail = getProjectStorageQuotaDetail(projectId, scopedToken);

		return ProjectServiceDto.from(
			databaseProject, openstackProject,
			projectComputeQuotaDetail, projectStorageQuotaDetail
		);
	}

	@Transactional
	public KeystoneProject updateProject(String projectId, UpdateProjectRequest updatedProjectRequest, String requesterId) {
		UpdateKeystoneProjectRequest updateRequest = UpdateKeystoneProjectRequest.builder()
				.name(updatedProjectRequest.name())
				.isDomain(updatedProjectRequest.isDomain())
				.description(updatedProjectRequest.description())
				.domainId(updatedProjectRequest.domainId())
				.enabled(updatedProjectRequest.enabled())
				.tags(updatedProjectRequest.tags())
				.build();

		String keystoneToken = authModule.getUnscopedTokenByUserId(requesterId);
		return keystoneAPIExternalPort.updateProject(projectId, keystoneToken, updateRequest);
	}

	@Transactional
	public void deleteProject(String projectId, String requesterId) {
		String keystoneToken = authModule.getUnscopedTokenByUserId(requesterId);

		keystoneAPIExternalPort.deleteProject(projectId, keystoneToken);
	}

	// ============ Quota ============

	public ProjectComputeQuotaDto getProjectComputeQuotaDetail(String projectId, String privilegedToken) {
		ResponseEntity<JsonNode> jsonNodeResponseEntity = computeQuotaExternalPort.callGetQuotaDetail(privilegedToken, projectId);
		return getComputeQuotaFromResponse(jsonNodeResponseEntity);
	}

	public ProjectStorageQuotaDto getProjectStorageQuotaDetail(String projectId, String privilegedToken) {
		ResponseEntity<JsonNode> jsonNodeResponseEntity = volumeQuotaExternalPort.callGetQuota(privilegedToken, projectId);
		return getStorageQuotaFromResponse(jsonNodeResponseEntity);
	}

	private static ProjectComputeQuotaDto getComputeQuotaFromResponse(ResponseEntity<JsonNode> jsonNodeResponseEntity) {
		JsonNode computeQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");

		QuotaInformation cpuInfo = getQuotaInformation(computeQuotaSet, "cores");
		QuotaInformation ramInfo = getQuotaInformation(computeQuotaSet, "ram");
		QuotaInformation instanceInfo = getQuotaInformation(computeQuotaSet, "instances");
		QuotaInformation keypairInfo = getQuotaInformation(computeQuotaSet, "key_pairs");

		return ProjectComputeQuotaDto.builder()
			.core(cpuInfo)
			.ram(ramInfo)
			.instance(instanceInfo)
			.keypair(keypairInfo)
			.build();
	}

	private static ProjectStorageQuotaDto getStorageQuotaFromResponse(ResponseEntity<JsonNode> jsonNodeResponseEntity) {
		JsonNode storageQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");

		QuotaInformation volumesCount = getQuotaInformation(storageQuotaSet, "volumes");
		QuotaInformation volumesSize = getQuotaInformation(storageQuotaSet, "gigabytes");

		QuotaInformation backupsCount = getQuotaInformation(storageQuotaSet, "backups");
		QuotaInformation backupsSize = getQuotaInformation(storageQuotaSet, "backup_gigabytes");

		QuotaInformation snapshotCount = getQuotaInformation(storageQuotaSet, "snapshots");

		return ProjectStorageQuotaDto.from(
			volumesCount, volumesSize,
			backupsCount, backupsSize,
			snapshotCount
		);
	}

	private static QuotaInformation getQuotaInformation(JsonNode computeQuotaSet, String infoType) {
		JsonNode cpuCoreCountQuota = computeQuotaSet.get(infoType);
		return QuotaInformation.builder()
			.available(cpuCoreCountQuota.get("limit").asInt())
			.used(cpuCoreCountQuota.get("in_use").asInt())
			.build();
	}

	public void updateProjectCPUAndRAMQuota(String adminToken, String projectId, int vCpuQuota, int ramQuota, String userId) {
		computeQuotaExternalPort.callUpdateCPUAndRAMQuota(
			adminToken,
			projectId, vCpuQuota, ramQuota
		);
	}

	public void updateProjectStorageQuota(String adminToken, String projectId, int storageQuota, String userId) {
		volumeQuotaExternalPort.callUpdateVolumeQuota(
			adminToken,
			projectId, storageQuota
		);

	}

	// ============ Participant ============
	public List<ProjectParticipantDto> getProjectParticipantList(String projectId) {
		List<ProjectParticipantEntity> projectParticipants = projectParticipantRepositoryPort.findByProjectId(projectId);
		return projectParticipants.stream()
			.map(ProjectParticipantDto::from)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	public void inviteParticipants(String projectId, List<String> userIds, ProjectRole role, String token) {
		checkUserIdsValid(userIds, token);
		applyProjectAccessOnOpenstack(projectId, userIds, role, token);
		applyProjectAccessOnDatabase(projectId, userIds, role);
	}

	public void kickOutParticipants(String projectId, List<String> userIds, String token) {
		checkUserIdsValid(userIds, token);
		retrieveProjectAccessOnOpenstack(projectId, userIds, token);
		retrieveProjectAccessOnDatabase(projectId, userIds);
	}

	public void applyProjectAccessOnOpenstack(String projectId, List<String> userIds, ProjectRole role, String token) {
		String projectRoleKeystoneId = keystoneAPIExternalPort.getProjectRole(role, token);
		for (String userId : userIds) {
			keystoneAPIExternalPort.assignProjectRole(userId, projectId, projectRoleKeystoneId, token);
		}
	}

	private void retrieveProjectAccessOnOpenstack(String projectId, List<String> userIds, String token) {
		List<ProjectParticipantEntity> retrievedProjectMappingList = projectParticipantRepositoryPort.findByProjectId(projectId).stream()
			.filter(v -> userIds.contains(v.getUserDetail().getUserId()))
			.toList();
		for (ProjectParticipantEntity projectRoleMap : retrievedProjectMappingList) {
			keystoneAPIExternalPort.retrieveProjectRole(
				projectRoleMap.getUserDetail().getUserId(),
				projectRoleMap.getProject().getProjectId(),
				keystoneAPIExternalPort.getProjectRole(projectRoleMap.getRole(), token),
				token
			);
		}
	}

	private void applyProjectAccessOnDatabase(String projectId, List<String> userIds, ProjectRole role) {
		ProjectEntity dbSavedProject = findDBSavedProjectById(projectId);
		List<ProjectParticipantEntity> newParticipantsMappingList = userRepositoryPort.findUserDetailsByIds(userIds).stream()
			.map(
				userDetailEntity -> ProjectParticipantEntity.builder()
					.projectParticipantId(
						new ProjectParticipantId(
							dbSavedProject.getProjectId(),
							userDetailEntity.getUserId()
						)
					)
					.project(dbSavedProject)
					.userDetail(userDetailEntity)
					.role(role)
					.build()
			)
			.toList();

		projectParticipantRepositoryPort.saveAll(newParticipantsMappingList);
	}

	private void retrieveProjectAccessOnDatabase(String projectId, List<String> userIds) {
		List<ProjectParticipantEntity> retrievedProjectMappingList = projectParticipantRepositoryPort.findByProjectId(projectId).stream()
			.filter(v -> userIds.contains(v.getUserDetail().getUserId()))
			.toList();
		projectParticipantRepositoryPort.deleteAll(retrievedProjectMappingList);
	}

	private boolean checkUserIdsValid(List<String> userIds, String token) {
		for (String userId: userIds) {
			boolean isUserValid = checkIsUserValid(userId, token);
			if (!isUserValid) {
				return false;
			}
		}
		return true;
	}

	private boolean checkIsUserValid(String userId, String token) {
		try {
			keystoneAPIExternalPort.getUserDetail(userId, token);
		} catch (KeystoneException e) {
			if (e.getErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
				return false;
			}
		}
		return true;
	}

	// TODO: REFACTOR - 너무 많은 요청; 이메일에 대한 db 캐싱을 통해 검색횟수 최소화 필요
	public List<InvitableUser> getInvitableUserList(String projectId, String keyword, String token) {
		List<InvitableUser> invitableUsersByEmail = findInvitableUsersByEmail(keyword, projectId, token);
		List<InvitableUser> invitableUsersByName = findInvitableUsersByName(keyword, projectId, token);
		invitableUsersByEmail.addAll(invitableUsersByName);

		return invitableUsersByEmail.subList(0, 10);
	}

	private List<InvitableUser> findInvitableUsersByEmail(String email, String projectId, String token) {
		ResponseEntity<JsonNode> listUsersOpenstackResponse = keystoneUserAPIModule.listUsers(token, null, 30, email);
		List<UserKeystoneDto> users = KeystoneAPIUtils.parseKeystoneUserListResponse(listUsersOpenstackResponse).getUserKeystoneDtos();

		List<UserDbExtraEntity> userDetailsByIds = userRepositoryPort.findUserDetailsByIds(users.stream().map(UserKeystoneDto::id).toList());

		List<InvitableUser> invitableUsers = new ArrayList<>();
		for (UserDbExtraEntity userDbExtraEntity : userDetailsByIds) {
			UserKeystoneDto matchUser = users.stream()
				.filter(v -> v.id().equals(userDbExtraEntity.getUserId()))
				.findFirst()
				.orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND));

			invitableUsers.add(
				InvitableUser.builder()
					.userEmail(matchUser.name())
					.userName(userDbExtraEntity.getUserName())
					.userId(userDbExtraEntity.getUserId())
					.build()
			);
		}

		return invitableUsers;
	}

	private List<InvitableUser> findInvitableUsersByName(String name, String projectId, String token) {
		List<UserDbExtraEntity> userDetailsByIds = userRepositoryPort.findUserByUserName(name);

		List<InvitableUser> invitableUsers = new ArrayList<>();
		for (UserDbExtraEntity userDbExtraEntity : userDetailsByIds) {
			ResponseEntity<JsonNode> listUsersOpenstackResponse = keystoneUserAPIModule.getUserDetail(userDbExtraEntity.getUserId(), token);
			UserKeystoneDto user = KeystoneAPIUtils.parseKeystoneUserResponse(listUsersOpenstackResponse);

			invitableUsers.add(
				InvitableUser.builder()
					.userEmail(user.name())
					.userName(userDbExtraEntity.getUserName())
					.userId(userDbExtraEntity.getUserId())
					.build()
			);
		}

		return invitableUsers;
	}

	private ProjectEntity findDBSavedProjectById(String projectId) {
		Optional<ProjectEntity> projectEntity = projectRepositoryPort.findById(projectId);
		return projectEntity.orElseThrow(() -> new ProjectServiceException(ProjectErrorCode.PROJECT_NOT_FOUND));
	}

	public ProjectRole getProjectRole(String projectId, String requestUserId) {
		Optional<ProjectParticipantEntity> byProjectIdAndParticipantId = projectParticipantRepositoryPort.findByProjectIdAndParticipantId(projectId, requestUserId);
		if (byProjectIdAndParticipantId.isPresent()) {
			return byProjectIdAndParticipantId.get().getRole();
		}

		throw new ProjectServiceException(ProjectErrorCode.FORBIDDEN_ACCESS, "접근권한이 없는 프로젝트입니다");
	}

	public void checkUserPrivilege(String projectId, String requestUserId) {
		ProjectRole requesterRole = getProjectRole(projectId, requestUserId);
		if (requesterRole != ProjectRole.PROJECT_ADMIN) {
			throw new ProjectServiceException(ProjectErrorCode.FORBIDDEN_ACCESS, "접근권한이 없는 요청입니다");
		}
	}

	public boolean isParticipateUserExists(String projectId, List<String> userIds) {
		List<String> projectParticipantIdList = getProjectParticipantList(projectId).stream()
			.map(ProjectParticipantDto::userId)
			.toList();

		List<String> notParticipatedUserIds = userIds.stream()
			.filter(userId -> !projectParticipantIdList.contains(userId))
			.toList();

		return notParticipatedUserIds.size() == userIds.size();
	}

	public boolean isNotParticipateUserExists(String projectId, List<String> userIds) {
		List<String> projectParticipantIdList = getProjectParticipantList(projectId).stream()
			.map(ProjectParticipantDto::userId)
			.toList();

		List<String> participatedUserIds = userIds.stream()
			.filter(projectParticipantIdList::contains)
			.toList();

		return participatedUserIds.size() == userIds.size();
	}

}
