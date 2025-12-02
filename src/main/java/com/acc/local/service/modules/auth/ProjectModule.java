package com.acc.local.service.modules.auth;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.dto.instance.InstanceQuotaResponse;
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
import com.acc.local.dto.project.ProjectGlobalQuotaDto;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.ProjectParticipantEntity;
import com.acc.local.entity.ProjectRequestEntity;
import com.acc.local.entity.UserDetailEntity;
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
				responseList.add(ProjectServiceDto.from(databaseProject, openstackProject));
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
			try {
				String projectId = openstackProject.getId();
				ProjectEntity databaseProject = getDatabaseProject(projectId);
				responseList.add(ProjectServiceDto.from(databaseProject, openstackProject));
			} catch (AuthServiceException e) {
				throw e;
			}
		}

		return ProjectListServiceDto.builder()
			.pagination(openstackProjectResponse.pageInfo())
			.projects(responseList)
			.build();
	}

	public List<ProjectServiceDto> getAllProjectListForUser(String keyword, String requestUserId, String unscopedToken) {
		ProjectListDto openstackProjectResponse = keystoneAPIExternalPort.getUserProjectsByProjectName(
			keyword,
			null, requestUserId,
			unscopedToken
		);

		List<ProjectServiceDto> responseList = new ArrayList<>();
		for (KeystoneProject openstackProject: openstackProjectResponse.projectList()) {
			try {
				String projectId = openstackProject.getId();
				ProjectEntity databaseProject = getDatabaseProject(projectId);
				responseList.add(ProjectServiceDto.from(databaseProject, openstackProject));
			} catch (AuthServiceException e) {
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

		Map<String, Object> projectRequest = KeystoneAPIUtils.createKeystoneCreateProjectRequest(project);
		ResponseEntity<JsonNode> response = keystoneAPIExternalPort.createProject(adminToken, projectRequest);

		if (response == null) {
			throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_CREATION_FAILED, "프로젝트 생성 응답이 null입니다.");
		}

		KeystoneProject keystoneSavedProject = KeystoneAPIUtils.parseKeystoneProjectResponse(response);
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

		UserDetailEntity projectOwnerUserDetail = userRepositoryPort.findUserDetailById(request.projectOwnerId())
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
	public ProjectServiceDto getProjectDetail(String projectId, String requestUserId) {
		String scopedToken = authModule.issueProjectScopeToken(projectId, requestUserId);

		ResponseEntity<JsonNode> response = keystoneAPIExternalPort.getProjectDetail(projectId, scopedToken);
		// authModule.invalidateServiceTokensByUserId(requestUserId);

		if (response == null) {
			throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, "프로젝트 조회 응답이 null입니다.");
		}

		KeystoneProject openstackProject = KeystoneAPIUtils.parseKeystoneProjectResponse(response);
		ProjectEntity databaseProject = getDatabaseProject(projectId);

		return ProjectServiceDto.from(databaseProject, openstackProject);
	}

	@Transactional
	public KeystoneProject updateProject(String projectId, UpdateProjectRequest updatedProjectRequest, String requesterId) {
		KeystoneProject project = KeystoneProject.builder()
			.name(updatedProjectRequest.name())
			.description(updatedProjectRequest.description())
			.build();

		String keystoneToken = authModule.getUnscopedTokenByUserId(requesterId);

		Map<String, Object> projectRequest = KeystoneAPIUtils.createKeystoneUpdateProjectRequest(project);
		ResponseEntity<JsonNode> response = keystoneAPIExternalPort.updateProject(projectId, keystoneToken, projectRequest);
		if (response == null) {
			throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_UPDATE_FAILED, "프로젝트 업데이트 응답이 null입니다.");
		}
		return KeystoneAPIUtils.parseKeystoneProjectResponse(response);
	}

	@Transactional
	public void deleteProject(String projectId, String requesterId) {
		String keystoneToken = authModule.getUnscopedTokenByUserId(requesterId);

		keystoneAPIExternalPort.deleteProject(projectId, keystoneToken);
	}

	// ============ Quota ============
	public ProjectGlobalQuotaDto getProjectQuota(String projectId, String adminToken) {
		ResponseEntity<JsonNode> jsonNodeResponseEntity = computeQuotaExternalPort.callGetQuota(adminToken, projectId);
		volumeQuotaExternalPort.callGetQuota(adminToken, projectId);
		JsonNode computeQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");
		JsonNode volumeQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");

		int cpuCoreCountQuota = computeQuotaSet.get("cores").asInt();
		int ramMBSizeQuota = computeQuotaSet.get("ram").asInt();
		int storageCountQuota = volumeQuotaSet.get("volumes").asInt();
		int instanceCountQuota = computeQuotaSet.get("instances").asInt();

		return ProjectGlobalQuotaDto.builder()
			.vCpu(cpuCoreCountQuota)
			.vRam(ramMBSizeQuota)
			.storage(storageCountQuota)
			.instance(instanceCountQuota)
			.build();
	}

	public InstanceQuotaResponse getProjectComputeQuotaDetail(String projectId, String adminToken) {
		ResponseEntity<JsonNode> jsonNodeResponseEntity = computeQuotaExternalPort.callGetQuotaDetail(adminToken, projectId);
		JsonNode computeQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");

		JsonNode cpuCoreCountQuota = computeQuotaSet.get("cores");
		JsonNode ramMBSizeQuota = computeQuotaSet.get("ram");
		JsonNode instanceCountQuota = computeQuotaSet.get("instances");
		JsonNode keypairCountQouta = computeQuotaSet.get("key_pairs");

		InstanceQuotaResponse.QuotaInformation cpuInfo = InstanceQuotaResponse.QuotaInformation.builder()
			.available(cpuCoreCountQuota.get("limit").asInt())
			.used(cpuCoreCountQuota.get("in_use").asInt())
			.build();
		InstanceQuotaResponse.QuotaInformation ramInfo = InstanceQuotaResponse.QuotaInformation.builder()
			.available(ramMBSizeQuota.get("limit").asInt())
			.used(ramMBSizeQuota.get("in_use").asInt())
			.build();
		InstanceQuotaResponse.QuotaInformation instanceInfo = InstanceQuotaResponse.QuotaInformation.builder()
			.available(instanceCountQuota.get("limit").asInt())
			.used(instanceCountQuota.get("in_use").asInt())
			.build();
		InstanceQuotaResponse.QuotaInformation keypairInfo = InstanceQuotaResponse.QuotaInformation.builder()
			.available(keypairCountQouta.get("limit").asInt())
			.used(keypairCountQouta.get("in_use").asInt())
			.build();
		return InstanceQuotaResponse.builder()
			.core(cpuInfo)
			.ram(ramInfo)
			.instance(instanceInfo)
			.keypair(keypairInfo)
			.build();
	}

	public void updateProjectCPUAndRAMQuota(String adminToken, String projectId, int vCpuQuota, int ramQuotaWithGBUnit, String userId) {
		computeQuotaExternalPort.callUpdateCPUAndRAMQuota(
			adminToken,
			projectId, vCpuQuota, ramQuotaWithGBUnit * 1024
		);
	}

	public void updateProjectStorageQuota(String adminToken, String projectId, int storageQuota, String userId) {
		volumeQuotaExternalPort.callUpdateVolumeQuota(
			adminToken,
			projectId, storageQuota
		);

	}

	public List<ProjectParticipantDto> getProjectParticipantList(String projectId) {
		List<ProjectParticipantEntity> projectParticipants = projectParticipantRepositoryPort.findByProjectId(projectId);
		return projectParticipants.stream()
			.map(ProjectParticipantDto::from)
			.toList();
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
			getUserBaseInfoFromKeystone(userId, token);
		} catch (KeystoneException e) {
			if (e.getErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
				return false;
			}
		}
		return true;
	}

	private KeystoneUser getUserBaseInfoFromKeystone(String userId, String token) {
		ResponseEntity<JsonNode> response = keystoneAPIExternalPort.getUserDetail(userId, token);
		if (response == null) {
			throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 조회 응답이 null입니다.");
		}

		return KeystoneAPIUtils.parseKeystoneUserResponse(response);
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
		List<KeystoneUser> users = KeystoneAPIUtils.parseKeystoneUserListResponse(listUsersOpenstackResponse).getKeystoneUsers();

		List<UserDetailEntity> userDetailsByIds = userRepositoryPort.findUserDetailsByIds(users.stream().map(KeystoneUser::getId).toList());

		List<InvitableUser> invitableUsers = new ArrayList<>();
		for (UserDetailEntity userDetailEntity : userDetailsByIds) {
			KeystoneUser matchUser = users.stream()
				.filter(v -> v.getId().equals(userDetailEntity.getUserId()))
				.findFirst()
				.orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND));

			invitableUsers.add(
				InvitableUser.builder()
					.userEmail(matchUser.getName())
					.userName(userDetailEntity.getUserName())
					.userId(userDetailEntity.getUserId())
					.build()
			);
		}

		return invitableUsers;
	}

	private List<InvitableUser> findInvitableUsersByName(String name, String projectId, String token) {
		List<UserDetailEntity> userDetailsByIds = userRepositoryPort.findUserByUserName(name);

		List<InvitableUser> invitableUsers = new ArrayList<>();
		for (UserDetailEntity userDetailEntity : userDetailsByIds) {
			ResponseEntity<JsonNode> listUsersOpenstackResponse = keystoneUserAPIModule.getUserDetail(userDetailEntity.getUserId(), token);
			KeystoneUser user = KeystoneAPIUtils.parseKeystoneUserResponse(listUsersOpenstackResponse);

			invitableUsers.add(
				InvitableUser.builder()
					.userEmail(user.getName())
					.userName(userDetailEntity.getUserName())
					.userId(userDetailEntity.getUserId())
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
