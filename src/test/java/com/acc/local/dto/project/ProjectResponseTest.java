package com.acc.local.dto.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProjectResponseTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void fromProjectServiceDtoSerializesQuotaWithoutDeprecatedProjectBrief() throws Exception {
		ProjectServiceDto project = ProjectServiceDto.builder()
			.projectId("project-id")
			.projectName("project-name")
			.projectType(ProjectRequestType.ETC)
			.status(ProjectRequestStatus.APPROVED)
			.quota(ProjectGlobalQuotaDto.getDefault())
			.build();
		UserKeystoneDto owner = UserKeystoneDto.builder()
			.id("owner-id")
			.name("owner")
			.build();

		ProjectResponse response = ProjectResponse.from(project, owner, new ArrayList<>());

		JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));
		assertThat(json.has("projectBrief")).isFalse();
		assertThat(json.has("quota")).isTrue();
	}

	@Test
	void fromProjectRequestDtoSerializesQuotaWithoutDeprecatedProjectBrief() throws Exception {
		ProjectRequestDto projectRequest = ProjectRequestDto.builder()
			.projectName("pending-project")
			.projectType(ProjectRequestType.CAPSTONE_DESIGN)
			.createdAt(LocalDateTime.of(2026, 6, 18, 10, 0))
			.status(ProjectRequestStatus.PENDING)
			.build();
		UserKeystoneDto requester = UserKeystoneDto.builder()
			.id("requester-id")
			.name("requester")
			.build();

		ProjectResponse response = ProjectResponse.from(projectRequest, requester);

		JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));
		assertThat(json.has("projectBrief")).isFalse();
		assertThat(json.has("quota")).isTrue();
	}
}
