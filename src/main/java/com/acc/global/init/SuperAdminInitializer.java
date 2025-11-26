package com.acc.global.init;

import com.acc.global.properties.SuperAdminProperties;
import com.acc.local.entity.UserAuthDetailEntity;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.repository.jpa.UserAuthDetailJpaRepository;
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
    private final UserAuthDetailJpaRepository userAuthDetailJpaRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String userId = superAdminProperties.getUserId();

        // 이미 존재하는지 체크 - 있으면 생성 안함
        if (userDetailJpaRepository.existsById(userId)) {
            log.info("슈퍼관리자 계정이 이미 존재합니다. ID: {}", userId);
            return;
        }

        registerSuperAdminUser(userId);
        // registerSuperAdminUserDepartInfo(userId);
    }

    private void registerSuperAdminUserDepartInfo(String userId) {
        userAuthDetailJpaRepository.save(
            UserAuthDetailEntity.builder()
                .userId(userId)
                .department("관리자_기본입력값")
                .authType(2)
                .studentId("200012345")
                .build()
        );
        log.info("슈퍼관리자 계정의 더미 재학정보가 생성되었습니다 ID: {}", userId);
    }

    private void registerSuperAdminUser(String userId) {
        // 슈퍼 관리자 계정 생성 - 없으면 생성
        log.info("슈퍼관리자 계정이 존재하지 않습니다 ID: {}", userId);
        String phoneNumber = superAdminProperties.getPhoneNumber();

        UserDetailEntity superAdmin = UserDetailEntity.builder()
                .userId(userId)
                .userPhoneNumber(phoneNumber)
                .isAdmin(true)
                .userName(superAdminProperties.getUserName())
                .build();
        userDetailJpaRepository.save(superAdmin);
        log.info("슈퍼관리자 계정이 생성되었습니다 ID: {}", userId);
    }
}
