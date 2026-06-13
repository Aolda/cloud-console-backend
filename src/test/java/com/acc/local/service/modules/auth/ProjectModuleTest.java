package com.acc.local.service.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.acc.global.common.PageRequest;
import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.ProjectRequestListServiceDto;
import com.acc.local.entity.ProjectRequestEntity;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @DisplayName("프로젝트 요청 목록은 다음 데이터가 있을 때 nextMarker와 last=false를 반환한다.")
    void givenFullPageAndNextData_whenGetProjectRequestList_thenReturnNextMarker() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setLimit(2);
        List<ProjectRequestEntity> firstPage = List.of(entity("request-1"), entity("request-2"));

        given(projectRequestRepositoryPort.findAllByKeyword("issue30", 0, 2)).willReturn(firstPage);
        given(projectRequestRepositoryPort.findAllByKeyword("issue30", 2, 1)).willReturn(List.of(entity("request-3")));

        ProjectRequestListServiceDto result = projectModule.getProjectRequestList("issue30", pageRequest);

        assertThat(result.projectRequests()).hasSize(2);
        assertThat(result.pagination().isFirst()).isTrue();
        assertThat(result.pagination().isLast()).isFalse();
        assertThat(result.pagination().nextMarker()).isEqualTo("Mg==");
        assertThat(result.pagination().prevMarker()).isNull();
        verify(projectRequestRepositoryPort).findAllByKeyword("issue30", 0, 2);
        verify(projectRequestRepositoryPort).findAllByKeyword("issue30", 2, 1);
    }

    @Test
    @DisplayName("프로젝트 요청 목록은 marker offset부터 조회하고 이전 페이지 marker를 반환한다.")
    void givenMarker_whenGetProjectRequestListForUser_thenReadFromOffset() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setMarker("Mg==");
        pageRequest.setLimit(2);

        given(projectRequestRepositoryPort.findAllByKeywordAndRequestUserId("issue30", "owner-id", 2, 2))
                .willReturn(List.of(entity("request-3")));

        ProjectRequestListServiceDto result = projectModule.getProjectRequestList("issue30", pageRequest, "owner-id");

        assertThat(result.projectRequests()).hasSize(1);
        assertThat(result.pagination().isFirst()).isFalse();
        assertThat(result.pagination().isLast()).isTrue();
        assertThat(result.pagination().nextMarker()).isNull();
        assertThat(result.pagination().prevMarker()).isEqualTo("MA==");
        verify(projectRequestRepositoryPort).findAllByKeywordAndRequestUserId("issue30", "owner-id", 2, 2);
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
