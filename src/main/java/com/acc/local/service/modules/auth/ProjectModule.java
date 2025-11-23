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
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.ProjectListServiceDto;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.dto.project.ProjectQuotaDto;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.ProjectParticipantEntity;
import com.acc.local.entity.ProjectRequestEntity;
import com.acc.local.external.dto.keystone.CreateKeystoneProjectRequest;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.external.ports.VolumeQuotaExternalPort;
import com.acc.local.external.ports.compute.ComputeQuotaExternalPort;
import com.acc.local.repository.ports.ProjectParticipantRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import com.acc.local.repository.ports.ProjectRequestRepositoryPort;
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

	private final ProjectRequestRepositoryPort projectRequestRepositoryPort;
	private final ProjectRepositoryPort projectRepositoryPort;
	private final ProjectParticipantRepositoryPort projectParticipantRepositoryPort;

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
		int offset = getOffsetFromMarker(pageRequest.getMarker());
		int size = pageRequest.getLimit();
		List<ProjectRequestEntity> savedProjectRequestList = projectRequestRepositoryPort.findAllByKeyword(
			searchKeyword,
			offset, size
		);

		return ProjectRequestListServiceDto.from(
			savedProjectRequestList.stream().map(ProjectRequestDto::from).toList(),
			pageRequest, false, null
		);
	}

	private static int getOffsetFromMarker(String marker) {
		Base64.Decoder decoder = Base64.getDecoder();
		return Integer.parseInt(new String(decoder.decode(marker), StandardCharsets.UTF_8));
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
				Optional<ProjectEntity> databaseProjectOrNull = projectRepositoryPort.findById(projectId);

				if (databaseProjectOrNull.isEmpty()) {
					throw new AuthServiceException(AuthErrorCode.PROJECT_NOT_FOUND, projectId);
				}

				ProjectEntity databaseProject = databaseProjectOrNull.get();
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
			.build();
		projectRepositoryPort.save(aoldaProject);

		return keystoneSavedProject;
	}

	@Transactional
	public KeystoneProject getProjectDetail(String projectId, String requesterId) {
		String keystoneToken = authModule.getUnscopedTokenByUserId(requesterId);

		ResponseEntity<JsonNode> response = keystoneAPIExternalPort.getProjectDetail(projectId, keystoneToken);
		if (response == null) {
			throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, "프로젝트 조회 응답이 null입니다.");
		}
		return KeystoneAPIUtils.parseKeystoneProjectResponse(response);
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
	public ProjectQuotaDto getProjectQuota(String projectId, String adminToken) {
		ResponseEntity<JsonNode> jsonNodeResponseEntity = computeQuotaExternalPort.callGetQuota(adminToken, projectId);
		volumeQuotaExternalPort.callGetQuota(adminToken, projectId);
		JsonNode computeQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");
		JsonNode volumeQuotaSet = jsonNodeResponseEntity.getBody().get("quota_set");

		int cpuCoreCountQuota = computeQuotaSet.get("cores").asInt();
		int ramGBSizeQuota = computeQuotaSet.get("ram").asInt();
		int storageCountQuota = volumeQuotaSet.get("volumes").asInt();
		int instanceCountQuota = computeQuotaSet.get("instances").asInt();

		return ProjectQuotaDto.builder()
			.vCpu(cpuCoreCountQuota)
			.vRam(ramGBSizeQuota)
			.storage(storageCountQuota)
			.instance(instanceCountQuota)
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
}
