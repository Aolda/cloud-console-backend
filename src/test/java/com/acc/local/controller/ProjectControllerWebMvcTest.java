package com.acc.local.controller;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.ProjectServicePort;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProjectServicePort projectServicePort;

	@MockBean
	private SessionRepositoryPort sessionRepositoryPort;

	@MockBean
	private SessionModule sessionModule;

	@Test
	void getProjectsReturnsPendingRequestParticipantPhoneNumber() throws Exception {
		SessionPrincipal principal = new SessionPrincipal("session-id", "keycloak-user-id", "requester-id");
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null);
		ProjectResponse response = ProjectResponse.builder()
			.projectName("pending-project")
			.projectType(ProjectRequestType.CAPSTONE_DESIGN)
			.status(ProjectRequestStatus.PENDING)
			.participants(List.of(
				ProjectParticipantDto.builder()
					.userId("requester-id")
					.userName("requester")
					.userPhoneNumber("010-1234-5678")
					.role(ProjectRole.PROJECT_ADMIN)
					.build()
			))
			.build();

		given(projectServicePort.getProjects(isNull(), eq("session-id"))).willReturn(List.of(response));

		mockMvc.perform(get("/api/v1/projects").principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].participants[0].userPhoneNumber").value("010-1234-5678"));
	}
}
