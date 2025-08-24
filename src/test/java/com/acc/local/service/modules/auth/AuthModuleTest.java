package com.acc.local.service.modules.auth;

import com.acc.global.security.JwtUtils;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthModuleTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserTokenRepositoryPort userTokenRepositoryPort;

    @Mock
    private KeystoneModule keystoneModule;

    @InjectMocks
    private AuthModule authModule;

    @Test
    @DisplayName("Keycloak 토큰으로 인증하고 JWT 토큰을 생성할 수 있다")
    void authenticateAndGenerateJwtTest() {
        // Given
        String keycloakToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MGIwM2U0Ny14eHh4LXh4eHgteHh4eC14eHh4eHh4eCJ9";
        String userId = "50b03e47-xxxx-xxxx-xxxx-xxxxxxxx";
        String keystoneToken = "14080769fe05e1f8b837fb43ca0f0ba4";
        String expectedJwtToken = "jwt-token-12345";

        when(jwtUtils.extractUserIdFromKeycloakToken(keycloakToken)).thenReturn(userId);
        when(keystoneModule.login(keycloakToken)).thenReturn(keystoneToken);
        when(jwtUtils.generateToken(userId, keystoneToken)).thenReturn(expectedJwtToken);
        when(userTokenRepositoryPort.save(any(UserTokenEntity.class))).thenReturn(mock(UserTokenEntity.class));

        // When
        String actualJwtToken = authModule.authenticateAndGenerateJwt(keycloakToken);

        // Then
        assertEquals(expectedJwtToken, actualJwtToken);
        verify(jwtUtils).extractUserIdFromKeycloakToken(keycloakToken);
        verify(userTokenRepositoryPort).deactivateAllByUserId(userId);
        verify(keystoneModule).login(keycloakToken);
        verify(jwtUtils).generateToken(userId, keystoneToken);
        verify(userTokenRepositoryPort).save(any(UserTokenEntity.class));
    }

    @Test
    @DisplayName("사용자 ID와 프로젝트 ID로 프로젝트 권한을 조회할 수 있다")
    void getProjectPermissionTest() {
        // Given
        String projectId = "project-123";
        String userId = "50b03e47-xxxx-xxxx-xxxx-xxxxxxxx";
        String keystoneToken = "14080769fe05e1f8b837fb43ca0f0ba4";
        ProjectPermission expectedPermission = ProjectPermission.ROOT;

        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(mockUserToken));
        when(keystoneModule.getPermission(keystoneToken, projectId)).thenReturn(expectedPermission);

        // When
        ProjectPermission actualPermission = authModule.getProjectPermission(projectId, userId);

        // Then
        assertEquals(expectedPermission, actualPermission);
        verify(userTokenRepositoryPort).findByUserIdAndIsActiveTrue(userId);
        verify(keystoneModule).getPermission(keystoneToken, projectId);
    }
}