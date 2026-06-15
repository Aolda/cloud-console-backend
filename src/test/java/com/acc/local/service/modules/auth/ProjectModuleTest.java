package com.acc.local.service.modules.auth;

import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.ProjectCreateDto;
import com.acc.local.dto.project.quota.ProjectQuotaRequest;
import com.acc.local.entity.ProjectEntity;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
}
