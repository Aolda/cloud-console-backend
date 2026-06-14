package com.acc.local.service.adapters.auth;

import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import com.acc.local.service.modules.auth.ProjectModule;
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

    @Mock
    private ProjectModule projectModule;

    @Mock
    private KeystoneTokenModule keystoneTokenModule;

    @InjectMocks
    private AuthServiceAdapter authServiceAdapter;

    @Test
    @DisplayName("레거시 프로필 조회에서 발급한 프로젝트 스코프 토큰은 사용 후 즉시 폐기한다.")
    void givenProjectId_whenGetUserLoginedProfile_thenRevokeScopedToken() {
        // given
        String userId = "user-id";
        String projectId = "project-id";
        given(authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile")).willReturn("admin-token");
        given(userModule.getUserById(userId, "admin-token")).willReturn(User.builder()
                .userId(userId)
                .username("user")
                .department("software")
                .build());
        given(authModule.issueProjectScopeToken(projectId, userId)).willReturn("scoped-token");
        given(projectModule.getProjectDetail(projectId, "scoped-token")).willReturn(ProjectServiceDto.builder()
                .projectId(projectId)
                .build());

        // when
        authServiceAdapter.getUserLoginedProfile(userId, projectId);

        // then
        then(keystoneTokenModule).should().revokeTokenQuietly("scoped-token");
        then(authModule).should().invalidateSystemAdminToken("admin-token");
    }
}
