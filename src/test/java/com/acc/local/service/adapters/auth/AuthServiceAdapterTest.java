package com.acc.local.service.adapters.auth;

import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.LoginedUserProfileResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.UserModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceAdapterTest {

    @Mock
    private AuthModule authModule;

    @Mock
    private UserModule userModule;

    @InjectMocks
    private AuthServiceAdapter authServiceAdapter;

    @Test
    void getUserLoginedProfileReturnsProfileWithoutProject() throws Exception {
        String userId = "user-id";
        String adminToken = "admin-token";
        User user = User.builder()
                .userId(userId)
                .username("현제 이")
                .department("소프트웨어및컴퓨터공학전공")
                .build();

        when(authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile")).thenReturn(adminToken);
        when(userModule.getUserById(userId, adminToken)).thenReturn(user);

        LoginedUserProfileResponse response = authServiceAdapter.getUserLoginedProfile(userId);
        String json = new ObjectMapper().writeValueAsString(response);

        assertEquals("현제 이", response.userName());
        assertEquals("소프트웨어및컴퓨터공학전공", response.univ().department());
        assertFalse(json.contains("project"));
        assertFalse(json.contains("univDepartment"));
        verify(authModule, never()).issueProjectScopeToken(anyString(), anyString());
        verify(authModule).invalidateSystemAdminToken(adminToken);
    }
}
