package com.acc.local.service.adapters.auth;

import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.LoginedUserProfileResponse;
import com.acc.local.dto.auth.UnivDepartBriefDto;
import com.acc.local.service.modules.auth.AuthModule;
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

    @Override
    public LoginedUserProfileResponse getUserLoginedProfile(String userId) {
        String adminToken = authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile");

        try {
            User user = userModule.getUserById(userId, adminToken);

            return LoginedUserProfileResponse.builder()
                    .userName(user.getUsername())
                    .univ(UnivDepartBriefDto.from(user))
                    .build();
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }
}
