package com.acc.local.service.adapters.quicksetting;

import com.acc.local.dto.quickcreate.QuickSettingFormResponse;
import com.acc.local.dto.quickcreate.QuickSettingRequest;
import com.acc.local.service.modules.quicksetting.QuickSettingModule;
import com.acc.local.service.ports.QuickSettingServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class QuickSettingServiceAdapter implements QuickSettingServicePort {

    private final QuickSettingModule quickSettingModule;

    public QuickSettingFormResponse getForm(String token, String projectId) {

        /* --- token 검증 ( 프로젝트, Role 권한 검증 ) --- */

        /* --- flavor 리스트 --- */

        return QuickSettingFormResponse.builder()
                .build();
    }

    public void create(String token, String projectId, QuickSettingRequest request) {

        /* --- token 검증 ( 프로젝트, Role 권한 검증 ) --- */

        /* --- dto 검증 --- */

        /* --- instance 생성 --- */

        /* --- network 생성 --- */

        /* --- 그 외 로직 및 예외 처리 --- */
    }
}
