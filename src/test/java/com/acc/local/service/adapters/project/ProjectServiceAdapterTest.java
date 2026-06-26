package com.acc.local.service.adapters.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.modules.session.SessionModule;

@ExtendWith(MockitoExtension.class)
class ProjectServiceAdapterTest {

	@Mock
	private ProjectModule projectModule;

	@Mock
	private AuthModule authModule;

	@Mock
	private UserModule userModule;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private SessionModule sessionModule;

	@InjectMocks
	private ProjectServiceAdapter projectServiceAdapter;

	@Test
	@DisplayName("프로젝트 목록에 포함된 생성 요청 participant는 요청자의 전화번호를 포함한다.")
	void givenPendingProjectRequest_whenGetProjects_thenParticipantHasPhoneNumber() {
		String sessionId = "session-id";
		String requestUserId = "requester-id";
		String unscopedToken = "unscoped-token";
		String adminToken = "admin-token";
		ProjectRequestDto pendingRequest = ProjectRequestDto.builder()
			.projectRequestId("request-id")
			.projectName("pending-project")
			.projectType(ProjectRequestType.CAPSTONE_DESIGN)
			.status(ProjectRequestStatus.PENDING)
			.createdAt(LocalDateTime.of(2026, 6, 18, 10, 0))
			.build();

		given(sessionModule.getKeystoneUserId(sessionId)).willReturn(requestUserId);
		given(sessionModule.getKeystoneUnscopedToken(sessionId)).willReturn(unscopedToken);
		given(authModule.issueSystemAdminTokenWithAdminProjectScope(requestUserId)).willReturn(adminToken);
		given(projectModule.getAllProjectListForUser("issue32", requestUserId, unscopedToken, adminToken))
			.willReturn(List.of());
		given(authModule.getUserDetail(requestUserId, requestUserId))
			.willReturn(UserKeystoneDto.builder().id(requestUserId).name("requester").build());
		given(userModule.adminGetUserDetailDB(requestUserId))
			.willReturn(UserDbExtraEntity.builder()
				.userId(requestUserId)
				.userName("requester")
				.userPhoneNumber("010-1234-5678")
				.build());
		given(projectModule.getAllProjectRequestList("issue32", requestUserId))
			.willReturn(List.of(pendingRequest));

		List<ProjectResponse> response = projectServiceAdapter.getProjects("issue32", sessionId);

		assertThat(response).hasSize(1);
		assertThat(response.get(0).participants()).hasSize(1);
		assertThat(response.get(0).participants().get(0).userPhoneNumber()).isEqualTo("010-1234-5678");
		then(authModule).should().invalidateSystemAdminToken(adminToken);
	}
}
