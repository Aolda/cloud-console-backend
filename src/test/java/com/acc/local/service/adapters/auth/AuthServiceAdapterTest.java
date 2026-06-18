package com.acc.local.service.adapters.auth;

import com.acc.local.domain.model.auth.User;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.UserModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceAdapterTest {

    @Mock
    private AuthModule authModule;

    @Mock
    private UserModule userModule;

    @InjectMocks
    private AuthServiceAdapter authServiceAdapter;

    @Test
    @DisplayName("프로필 조회는 사용자 기본 정보만 반환한다.")
    void getUserLoginedProfileReturnsUserProfile() {
        String userId = "user-id";
        given(authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile")).willReturn("admin-token");
        given(userModule.getUserById(userId, "admin-token")).willReturn(User.builder()
                .userId(userId)
                .username("user")
                .department("software")
                .build());
        authServiceAdapter.getUserLoginedProfile(userId);

        then(authModule).should().invalidateSystemAdminToken("admin-token");
    }
}
