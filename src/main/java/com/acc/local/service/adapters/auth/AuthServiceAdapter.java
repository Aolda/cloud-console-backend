package com.acc.local.service.adapters.auth;

import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.LoginedUserProfileResponse;
import com.acc.local.dto.auth.UnivDepartBriefDto;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.ports.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {

    private final AuthModule authModule;
    private final UserModule userModule;
    private final ProjectModule projectModule;
    private final KeystoneTokenModule keystoneTokenModule;

    @Override
    public LoginedUserProfileResponse getUserLoginedProfile(String userId, String projectId) {
        String adminToken = authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile");

        try {
            // Module에서 User 도메인 모델 조회 (정합성 불일치 시 예외 발생)
            //TODO: 추후 정합성 맞추는 Flow 필요시 진행
            User user = userModule.getUserById(userId, adminToken);

            // projectId가 존재하면 프로젝트 정보 조회
            ProjectServiceDto projectServiceDto = null;
            if (projectId != null && !projectId.isBlank()) {
                String scopedToken = authModule.issueProjectScopeToken(projectId, userId);
                try {
                    projectServiceDto = projectModule.getProjectDetail(projectId, scopedToken);
                } finally {
                    keystoneTokenModule.revokeTokenQuietly(scopedToken);
                }
            }

            return LoginedUserProfileResponse.builder()
                .userName(user.getUsername())
                .univ(UnivDepartBriefDto.from(user))
                .project(projectServiceDto)
                .build();
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }
}
