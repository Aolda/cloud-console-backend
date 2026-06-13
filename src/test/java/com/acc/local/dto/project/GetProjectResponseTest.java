package com.acc.local.dto.project;

import static org.assertj.core.api.Assertions.assertThat;

import com.acc.local.domain.enums.project.ProjectRequestType;
import org.junit.jupiter.api.Test;

class GetProjectResponseTest {

	@Test
	void fromDefaultsNullProjectTypeToEtc() {
		ProjectServiceDto project = ProjectServiceDto.builder()
			.projectId("project-id")
			.projectName("project-name")
			.projectType(null)
			.build();

		GetProjectResponse response = GetProjectResponse.from(project);

		assertThat(response.projectType()).isEqualTo(ProjectRequestType.ETC);
	}

	@Test
	void fromKeepsExistingProjectType() {
		ProjectServiceDto project = ProjectServiceDto.builder()
			.projectId("project-id")
			.projectName("project-name")
			.projectType(ProjectRequestType.CAPSTONE_DESIGN)
			.build();

		GetProjectResponse response = GetProjectResponse.from(project);

		assertThat(response.projectType()).isEqualTo(ProjectRequestType.CAPSTONE_DESIGN);
	}
}
