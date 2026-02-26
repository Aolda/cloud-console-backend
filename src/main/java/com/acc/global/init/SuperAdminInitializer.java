package com.acc.global.init;

import com.acc.global.properties.SuperAdminProperties;
import com.acc.global.security.crypto.KeystonePasswordEncryptor;
import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.id.UserIdentityId;
import com.acc.local.repository.jpa.UserIdentityJpaRepository;
import com.acc.local.repository.jpa.UserDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements ApplicationRunner {

    private final SuperAdminProperties superAdminProperties;
    private final UserDetailJpaRepository userDetailJpaRepository;
    private final UserIdentityJpaRepository userIdentityJpaRepository;
    private final KeystonePasswordEncryptor keystonePasswordEncryptor;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String userId = superAdminProperties.getUserId();

        // 이미 존재하는지 체크 - keycloakUserId가 없으면 업데이트, 있으면 종료
        if (userDetailJpaRepository.existsById(userId)) {
            userDetailJpaRepository.findById(userId).ifPresent(existing -> {
                if (existing.getKeycloakUserId() == null || existing.getKeycloakUserId().isBlank()) {
                    userDetailJpaRepository.save(existing.toBuilder()
                            .keycloakUserId(superAdminProperties.getKeycloakUserId())
                            .keystoneUsername(superAdminProperties.getKeystoneUsername())
                            .keystonePassword(keystonePasswordEncryptor.encrypt(
                                    superAdminProperties.getKeystonePassword()))
                            .build());
                    log.info("슈퍼관리자 Keycloak 연결 정보 업데이트 완료 ID: {}", userId);
                } else {
                    log.info("슈퍼관리자 계정이 이미 존재합니다. ID: {}", userId);
                }
            });
            registerSuperAdminUserIdentity(userId);
            return;
        }

        registerSuperAdminUser(userId);
        registerSuperAdminUserIdentity(userId);
    }

    private void registerSuperAdminUserIdentity(String userId) {
        boolean alreadyExists = userIdentityJpaRepository
                .existsById(new UserIdentityId(userId, AuthType.ADMIN.getCode()));
        if (alreadyExists) {
            log.info("슈퍼관리자 identity가 이미 존재합니다 ID: {}", userId);
            return;
        }
        userIdentityJpaRepository.save(
            UserIdentityEntity.builder()
                .id(new UserIdentityId(userId, AuthType.ADMIN.getCode()))
                .department("관리자")
                .studentId("200012345")
                .userEmail(superAdminProperties.getKeystoneUsername())
                .build()
        );
        log.info("슈퍼관리자 identity가 생성되었습니다 ID: {}", userId);
    }

    private void registerSuperAdminUser(String userId) {
        // 슈퍼 관리자 계정 생성 - 없으면 생성
        log.info("슈퍼관리자 계정이 존재하지 않습니다 ID: {}", userId);
        String phoneNumber = superAdminProperties.getPhoneNumber();

        UserDbExtraEntity superAdmin = UserDbExtraEntity.builder()
                .userId(userId)
                .userPhoneNumber(phoneNumber)
                .isAdmin(true)
                .userName(superAdminProperties.getUserName())
                .keycloakUserId(superAdminProperties.getKeycloakUserId())
                .keystoneUsername(superAdminProperties.getKeystoneUsername())
                .keystonePassword(keystonePasswordEncryptor.encrypt(
                        superAdminProperties.getKeystonePassword()))
                .build();
        userDetailJpaRepository.save(superAdmin);
        log.info("슈퍼관리자 계정이 생성되었습니다 ID: {}", userId);
    }
}
