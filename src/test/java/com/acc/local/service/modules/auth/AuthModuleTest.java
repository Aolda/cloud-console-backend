package com.acc.local.service.modules.auth;

import com.acc.global.security.JwtUtils;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

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
    @DisplayName("유효한 keycloak 토큰으로 JWT 토큰이 생성된다")
    void generateJwtWithKeystoneToken_Success() {
        // Given
        String userId = "testUser";
        String keycloakToken = "valid-keycloak-token";
        String keystoneToken = "keystone-token-123";
        String expectedJwtToken = "jwt-token-123";

        when(keystoneModule.login(keycloakToken))
                .thenReturn(Mono.just(keystoneToken));
        when(jwtUtils.generateToken(userId, keystoneToken))
                .thenReturn(expectedJwtToken);

        // When
        String result = authModule.generateJwtWithKeystoneToken(userId, keycloakToken).block();

        // Then
        assertEquals(expectedJwtToken, result);
    }
}