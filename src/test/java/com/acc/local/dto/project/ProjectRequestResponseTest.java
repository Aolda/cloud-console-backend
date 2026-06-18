package com.acc.local.dto.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProjectRequestResponseTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void fromSerializesQuotaWithoutDeprecatedProjectBrief() throws Exception {
		ProjectRequestDto projectRequest = ProjectRequestDto.builder()
			.projectRequestId("request-id")
			.projectName("project-name")
			.projectType(ProjectRequestType.ETC)
			.createdAt(LocalDateTime.of(2026, 6, 18, 10, 10))
			.status(ProjectRequestStatus.PENDING)
			.quota(ProjectGlobalQuotaDto.getDefault())
			.build();
		UserKeystoneDto requester = UserKeystoneDto.builder()
			.id("requester-id")
			.name("requester")
			.build();

		ProjectRequestResponse response = ProjectRequestResponse.from(projectRequest, requester);

		JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));
		assertThat(json.has("projectBrief")).isFalse();
		assertThat(json.has("quota")).isTrue();
	}
}
