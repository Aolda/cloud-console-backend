package com.acc.global.init;

import com.acc.global.properties.SuperAdminProperties;
import com.acc.local.entity.UserDetailEntity;
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

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String userId = superAdminProperties.getUserId();

        // 이미 존재하는지 체크 - 있으면 생성 안함
        if (userDetailJpaRepository.existsById(userId)) {
            log.info("슈퍼관리자 계정이 이미 존재합니다. ID: {}", userId);
            return;
        }

        // 슈퍼 관리자 계정 생성 - 없으면 생성
        String phoneNumber = superAdminProperties.getPhoneNumber();

        UserDetailEntity superAdmin = UserDetailEntity.builder()
                .userId(userId)
                .userPhoneNumber(phoneNumber)
                .isAdmin(true)
                .userName(superAdminProperties.getUserName())
                .build();

        userDetailJpaRepository.save(superAdmin);
        log.info("슈퍼관리자 계정이 존재하지 않습니다 ID: {}", userId);
    }
}