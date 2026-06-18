package com.acc.local.service.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import com.acc.global.common.PageRequest;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.ProjectCreateDto;
import com.acc.local.dto.project.ProjectRequestDto;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.dto.project.quota.ProjectQuotaRequest;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.ProjectRequestEntity;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.dto.keystone.CreateKeystoneProjectRequest;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.external.modules.keystone.KeystoneUserAPIModule;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.external.ports.VolumeQuotaExternalPort;
import com.acc.local.external.ports.compute.ComputeQuotaExternalPort;
import com.acc.local.repository.ports.ProjectParticipantRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import com.acc.local.repository.ports.ProjectRequestRepositoryPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectModuleTest {

    @Mock
    private AuthModule authModule;

    @Mock
    private VolumeQuotaExternalPort volumeQuotaExternalPort;

    @Mock
    private ComputeQuotaExternalPort computeQuotaExternalPort;

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @Mock
    private ProjectRequestRepositoryPort projectRequestRepositoryPort;

    @Mock
    private ProjectParticipantRepositoryPort projectParticipantRepositoryPort;

    @Mock
    private KeystoneUserAPIModule keystoneUserAPIModule;

    @InjectMocks
    private ProjectModule projectModule;

    @Test
    @DisplayName("프로젝트 요청 목록은 다음 데이터가 있을 때 현재 페이지 마지막 ID를 nextMarker로 반환한다.")
    void givenFullPageAndNextData_whenGetProjectRequestList_thenReturnNextMarker() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setLimit(2);
        List<ProjectRequestEntity> fetchedPage = List.of(
                entity("request-1"),
                entity("request-2"),
                entity("request-3")
        );

        given(projectRequestRepositoryPort.findAllByKeyword("issue30", null, PageRequest.Direction.next, 3))
                .willReturn(fetchedPage);

        ProjectRequestListServiceDto result = projectModule.getProjectRequestList("issue30", pageRequest);

        assertThat(result.projectRequests()).hasSize(2);
        assertThat(result.projectRequests()).extracting(ProjectRequestDto::projectRequestId)
                .containsExactly("request-1", "request-2");
        assertThat(result.pagination().isFirst()).isTrue();
        assertThat(result.pagination().isLast()).isFalse();
        assertThat(result.pagination().nextMarker()).isEqualTo("request-2");
        assertThat(result.pagination().prevMarker()).isNull();
        verify(projectRequestRepositoryPort).findAllByKeyword("issue30", null, PageRequest.Direction.next, 3);
    }

    @Test
    @DisplayName("프로젝트 요청 목록은 marker ID 이후부터 조회하고 현재 페이지 첫 ID를 prevMarker로 반환한다.")
    void givenMarker_whenGetProjectRequestListForUser_thenReadFromMarker() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setMarker("request-2");
        pageRequest.setLimit(2);

        given(projectRequestRepositoryPort.findAllByKeywordAndRequestUserId(
                "issue30", "owner-id", "request-2", PageRequest.Direction.next, 3))
                .willReturn(List.of(entity("request-3")));

        ProjectRequestListServiceDto result = projectModule.getProjectRequestList("issue30", pageRequest, "owner-id");

        assertThat(result.projectRequests()).hasSize(1);
        assertThat(result.projectRequests()).extracting(ProjectRequestDto::projectRequestId)
                .containsExactly("request-3");
        assertThat(result.pagination().isFirst()).isFalse();
        assertThat(result.pagination().isLast()).isTrue();
        assertThat(result.pagination().nextMarker()).isNull();
        assertThat(result.pagination().prevMarker()).isEqualTo("request-3");
        verify(projectRequestRepositoryPort).findAllByKeywordAndRequestUserId(
                "issue30", "owner-id", "request-2", PageRequest.Direction.next, 3);
    }

    @Test
    @DisplayName("프로젝트 요청 목록은 direction=prev이면 marker 이전 ID 목록을 정방향으로 반환한다.")
    void givenPrevDirection_whenGetProjectRequestList_thenReadPreviousPageMarker() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setMarker("request-5");
        pageRequest.setLimit(2);
        pageRequest.setDirection(PageRequest.Direction.prev);
        List<ProjectRequestEntity> previousPage = List.of(
                entity("request-4"),
                entity("request-3"),
                entity("request-2")
        );

        given(projectRequestRepositoryPort.findAllByKeyword(
                "issue30", "request-5", PageRequest.Direction.prev, 3))
                .willReturn(previousPage);

        ProjectRequestListServiceDto result = projectModule.getProjectRequestList("issue30", pageRequest);

        assertThat(result.projectRequests()).hasSize(2);
        assertThat(result.projectRequests()).extracting(ProjectRequestDto::projectRequestId)
                .containsExactly("request-3", "request-4");
        assertThat(result.pagination().isFirst()).isFalse();
        assertThat(result.pagination().isLast()).isFalse();
        assertThat(result.pagination().nextMarker()).isEqualTo("request-4");
        assertThat(result.pagination().prevMarker()).isEqualTo("request-3");
        verify(projectRequestRepositoryPort).findAllByKeyword(
                "issue30", "request-5", PageRequest.Direction.prev, 3);
    }

    @Test
    @DisplayName("프로젝트 생성 시 요청 타입을 ProjectEntity에 저장한다.")
    void givenProjectCreateDtoWithProjectType_whenCreateProject_thenSaveProjectType() {
        // given
        String adminToken = "admin-token";
        String ownerUserId = "owner-user-id";
        ProjectCreateDto request = ProjectCreateDto.builder()
                .projectName("project")
                .projectDescription("description")
                .projectType(ProjectRequestType.MAJOR_LECTURE)
                .projectOwnerId(ownerUserId)
                .quota(ProjectQuotaRequest.builder()
                        .vCpu(4)
                        .vRam(8192)
                        .storage(100)
                        .instance(2)
                        .build())
                .build();
        KeystoneProject createdProject = KeystoneProject.builder()
                .id("created-project-id")
                .name("project")
                .build();

        given(keystoneAPIExternalPort.createProject(eq(adminToken), any(CreateKeystoneProjectRequest.class)))
                .willReturn(createdProject);
        given(userRepositoryPort.findUserDetailById(ownerUserId))
                .willReturn(Optional.of(UserDbExtraEntity.builder().userId(ownerUserId).build()));

        // when
        projectModule.createProject(adminToken, request, "admin-user-id");

        // then
        ArgumentCaptor<ProjectEntity> projectCaptor = ArgumentCaptor.forClass(ProjectEntity.class);
        then(projectRepositoryPort).should().save(projectCaptor.capture());
        assertThat(projectCaptor.getValue().getProjectType()).isEqualTo(ProjectRequestType.MAJOR_LECTURE);
    }

    private ProjectRequestEntity entity(String id) {
        return ProjectRequestEntity.builder()
                .projectRequestId(id)
                .requestUserId("owner-id")
                .projectName("issue30-project")
                .projectType(ProjectRequestType.ETC)
                .status(ProjectRequestStatus.PENDING)
                .projectDescription("description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
