package com.acc.local.service.modules.auth;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.global.exception.auth.UserLoginException;
import com.acc.global.properties.OpenStackProperties;
import com.acc.global.security.JwtUtils;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthModule {
    //global
    private final JwtUtils jwtUtils;
    //repository
    private final UserTokenRepositoryPort userTokenRepositoryPort;
    //module
    private final KeystoneModule keystoneModule;



    @Transactional
    public String authenticateAndGenerateJwt(String keycloakToken) {
        // Keycloak 토큰에서 사용자 ID 추출
        String userId = jwtUtils.extractUserIdFromKeycloakToken(keycloakToken);

        // 기존 사용자 토큰 비활성화
        userTokenRepositoryPort.deactivateAllByUserId(userId);

        // Keystone 토큰 발급
        String keystoneToken = keystoneModule.login(keycloakToken);

        // JWT 토큰 생성 (사용자 ID 사용)
        String jwtToken = jwtUtils.generateToken(userId, keystoneToken);

        // JWT 만료 시간 계산
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(86400); // 24시간 기본값

        // DB에 토큰 저장
        UserTokenEntity tokenEntity = UserTokenEntity.create(userId, jwtToken, keystoneToken, expiresAt);
        userTokenRepositoryPort.save(tokenEntity);

        return jwtToken;

    }

    public boolean validateJwtToken(String jwtToken) {
        // JWT 자체 유효성 검증
        if (!jwtUtils.validateToken(jwtToken)) {
            return false;
        }
        
        // DB에서 토큰 활성 상태 확인
        return userTokenRepositoryPort.findByJwtTokenAndIsActiveTrue(jwtToken)
                .map(UserTokenEntity::isValid)
                .orElse(false);
    }

    // 토큰 비활성화
    @Transactional
    public void invalidateUserTokens(String userId) {
        userTokenRepositoryPort.deactivateAllByUserId(userId);
    }

    public String getKeystoneTokenFromJwt(String jwtToken) {
        return userTokenRepositoryPort.findByJwtTokenAndIsActiveTrue(jwtToken)
                .map(UserTokenEntity::getKeystoneToken)
                .orElse(null);
    }

    public ProjectPermission getProjectPermission(String projectId, String userId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneToken();
        return keystoneModule.getPermission(keystoneToken, projectId);
    }

}
