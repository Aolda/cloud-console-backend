package com.acc.local.service.adapters.project;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.DecideProjectRequestResponse;
import com.acc.local.dto.project.ProjectListServiceDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.RepositoryPagination;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.modules.session.SessionModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AdminProjectServiceAdapterTest {

    @Mock
    private AuthModule authModule;

    @Mock
    private ProjectModule projectModule;

    @Mock
    private NeutronModule neutronModule;

    @Mock
    private KeystoneTokenModule keystoneTokenModule;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SessionModule sessionModule;

    @InjectMocks
    private AdminProjectServiceAdapter adminProjectServiceAdapter;

    @Test
    @DisplayName("관리자 프로젝트 요청 목록은 모듈 pagination 정보를 API 응답으로 보존한다.")
    void givenProjectRequestPagination_whenGetProjectRequests_thenReturnMarkers() {
        String sessionId = "session-id";
        String adminUserId = "admin-user-id";
        String ownerUserId = "owner-user-id";
        PageRequest pageRequest = new PageRequest();
        pageRequest.setLimit(2);
        ProjectRequestDto request = createPendingProjectRequest(ownerUserId);
        ProjectRequestListServiceDto listServiceDto = ProjectRequestListServiceDto.builder()
                .pagination(RepositoryPagination.builder()
                        .isFirst(true)
                        .isLast(false)
                        .nextMarker("request-id")
                        .prevMarker(null)
                        .build())
                .projectRequests(List.of(request))
                .build();

        given(sessionModule.getKeystoneUserId(sessionId)).willReturn(adminUserId);
        given(projectModule.getProjectRequestList("issue30", pageRequest)).willReturn(listServiceDto);
        given(authModule.getUserDetail(ownerUserId, adminUserId))
                .willReturn(UserKeystoneDto.builder().id(ownerUserId).name("owner@ajou.ac.kr").build());

        PageResponse<ProjectRequestResponse> response =
                adminProjectServiceAdapter.getProjectRequests("issue30", pageRequest, sessionId);

        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getFirst()).isTrue();
        assertThat(response.getLast()).isFalse();
        assertThat(response.getNextMarker()).isEqualTo("request-id");
        assertThat(response.getPrevMarker()).isNull();
    }

    @Test
    @DisplayName("프로젝트 요청 승인 시 프로젝트 소유자 credential로 작업용 스코프 토큰을 발급한다.")
    void givenPendingProjectRequest_whenApproveProjectRequest_thenUseOwnerCredentialScopedToken() {
        // given
        String sessionId = "session-id";
        String adminUserId = "admin-user-id";
        String ownerUserId = "owner-user-id";
        String adminToken = "admin-token";
        String ownerToken = "owner-scoped-token";
        String projectId = "created-project-id";
        ProjectRequestDto request = createPendingProjectRequest(ownerUserId);
        KeystoneProject createdProject = KeystoneProject.builder().id(projectId).name("project").build();

        given(sessionModule.getKeystoneUserId(sessionId)).willReturn(adminUserId);
        given(authModule.issueSystemAdminTokenWithAdminProjectScope(adminUserId)).willReturn(adminToken);
        given(projectModule.getProjectRequestList(List.of("request-id"))).willReturn(List.of(request));
        given(projectModule.createProject(eq(adminToken), any(), eq(adminUserId))).willReturn(createdProject);
        given(keystoneTokenModule.issueScopedTokenByUserCredentials(ownerUserId, projectId)).willReturn(ownerToken);

        // when
        DecideProjectRequestResponse response = adminProjectServiceAdapter.applyProjectRequestDecisions(
                List.of("request-id"), ProjectRequestStatus.APPROVED, null, sessionId);

        // then
        assertThat(response.applied()).isEqualTo(1);
        then(neutronModule).should().createDefaultNetwork(ownerToken);
        then(keystoneTokenModule).should().revokeTokenQuietly(ownerToken);
        then(authModule).should(never()).issueProjectScopeToken(anyString(), anyString());
        then(authModule).should().invalidateSystemAdminToken(adminToken);
    }

    @Test
    @DisplayName("프로젝트 승인 실패 후 복구 시 승인자의 legacy user_tokens를 조회하지 않는다.")
    void givenDefaultNetworkFailure_whenApproveProjectRequest_thenRollbackWithAdminToken() {
        // given
        String sessionId = "session-id";
        String adminUserId = "admin-user-id";
        String ownerUserId = "owner-user-id";
        String adminToken = "admin-token";
        String ownerToken = "owner-scoped-token";
        String projectId = "created-project-id";
        ProjectRequestDto request = createPendingProjectRequest(ownerUserId);
        KeystoneProject createdProject = KeystoneProject.builder().id(projectId).name("project").build();
        ProjectServiceDto rollbackProject = createRollbackProject(projectId, adminUserId);

        given(sessionModule.getKeystoneUserId(sessionId)).willReturn(adminUserId);
        given(authModule.issueSystemAdminTokenWithAdminProjectScope(adminUserId)).willReturn(adminToken);
        given(projectModule.getProjectRequestList(List.of("request-id"))).willReturn(List.of(request));
        given(projectModule.createProject(eq(adminToken), any(), eq(adminUserId))).willReturn(createdProject);
        given(keystoneTokenModule.issueScopedTokenByUserCredentials(ownerUserId, projectId)).willReturn(ownerToken);
        willThrow(new RuntimeException("network failed")).given(neutronModule).createDefaultNetwork(ownerToken);
        given(projectModule.getProjectRequest("request-id")).willReturn(request);
        given(projectModule.getProjectList("project", null, adminToken))
                .willReturn(ProjectListServiceDto.builder().projects(List.of(rollbackProject)).build());

        // when
        DecideProjectRequestResponse response = adminProjectServiceAdapter.applyProjectRequestDecisions(
                List.of("request-id"), ProjectRequestStatus.APPROVED, null, sessionId);

        // then
        assertThat(response.applied()).isZero();
        then(projectModule).should().deleteProjectWithToken(projectId, adminToken);
        then(authModule).should(never()).getUnscopedTokenByUserId(anyString());
        then(authModule).should().invalidateSystemAdminToken(adminToken);
    }

    private ProjectRequestDto createPendingProjectRequest(String ownerUserId) {
        return ProjectRequestDto.builder()
                .projectRequestId("request-id")
                .projectName("project")
                .requestUserId(ownerUserId)
                .description("description")
                .status(ProjectRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .projectBrief(ProjectGlobalQuotaDto.getDefault())
                .build();
    }

    private ProjectServiceDto createRollbackProject(String projectId, String adminUserId) {
        return ProjectServiceDto.builder()
                .projectId(projectId)
                .projectName("project")
                .description(ProjectRequestDto.getProjectDescriptionMessage("request-id", adminUserId))
                .build();
    }
}
