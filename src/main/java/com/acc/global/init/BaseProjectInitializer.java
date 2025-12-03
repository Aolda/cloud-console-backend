package com.acc.global.init;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.acc.global.properties.SuperAdminProperties;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.jpa.ProjectJpaRepository;
import com.acc.local.repository.jpa.UserDetailJpaRepository;
import com.acc.local.service.modules.auth.AuthModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseProjectInitializer implements ApplicationRunner {

    private final SuperAdminProperties superAdminProperties;
    private final UserDetailJpaRepository userDetailJpaRepository;

    private final ProjectJpaRepository projectJpaRepository;
    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final AuthModule authModule;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String token = authModule.issueSystemAdminToken("INITIALIZER");
        String adminProjectId = keystoneAPIExternalPort.getAdminProjectId(token);
        ProjectListDto keystoneProjects = keystoneAPIExternalPort.getProjectsByProjectName(null, null, token);
        authModule.invalidateSystemAdminToken(token);
        String userId = superAdminProperties.getUserId();

        checkIsAdminProjectExists(adminProjectId, userId);
        syncProjectSaveStatus(keystoneProjects);
    }

    private void syncProjectSaveStatus(ProjectListDto keystoneProjects) {
        ProjectGlobalQuotaDto defaultQuota = ProjectGlobalQuotaDto.getDefault();

        for (KeystoneProject keystoneProject: keystoneProjects.projectList()) {
            if (projectJpaRepository.findById(keystoneProject.getId()).isEmpty()) {
                log.info("프로젝트 정합성을 위한 프로젝트 자동등록: {}", keystoneProject);
                projectJpaRepository.save(ProjectEntity.builder()
                    .projectId(keystoneProject.getId())
                    .projectType(ProjectRequestType.ETC)
                    .createdAt(LocalDateTime.of(1900, 1, 1, 0, 0))
                    .quotaVRamMB((long)defaultQuota.ram().available())
                    .quotaVCpuCount((long)defaultQuota.core().available())
                    .quotaInstanceCount((long)defaultQuota.instance().available())
                    .quotaStorageGB((long)defaultQuota.volume().size().available())
                    .build()
                );
            }
        }
    }

    private void checkIsAdminProjectExists(String adminProjectId, String userId) {
        // 이미 존재하는지 체크 - 있으면 생성 안함
        if (projectJpaRepository.existsById(adminProjectId)) {
            log.info("관리자 프로젝트가 이미 등록되어있습니다. ID: {}", adminProjectId);
            return;
        }

        ProjectEntity aoldaProject = ProjectEntity.builder()
            .projectId(adminProjectId)
            .ownerKeystoneId(userId)
            .build();
        projectJpaRepository.save(aoldaProject);

        log.info("관리자 프로젝트를 등록했습니다. ID: {}", adminProjectId);
    }
}
