package com.acc.global.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.acc.global.properties.SuperAdminProperties;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.UserDetailEntity;
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
        authModule.invalidateSystemAdminToken(token);
        String userId = superAdminProperties.getUserId();

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
