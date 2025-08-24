package com.acc.local.service.modules.auth;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.global.properties.OpenStackProperties;
import com.acc.global.security.JwtUtils;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.entity.UserEntity;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.ports.UserRepositoryPort;
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
    private final UserRepositoryPort userRepositoryPort;
    private final UserTokenRepositoryPort userTokenRepositoryPort;
    //module
    private final KeystoneModule keystoneModule;



    @Transactional
    public Mono<String> generateJwtWithKeystoneToken(String userId, String keycloakToken) {
        // 사용자 찾기 또는 생성
        UserEntity user = userRepositoryPort.findByUserId(userId)
                .orElseGet(() -> userRepositoryPort.save(UserEntity.create(userId)));
        
        // 기존 사용자 토큰 비활성화
        userTokenRepositoryPort.deactivateAllByUserId(user.getId());
        
        // Keystone Federate Authentication으로 사용자 토큰 발급 후 JWT 생성
        return keystoneModule.login(keycloakToken)
                .map(keystoneToken -> {
                    // JWT 토큰 생성 (UserEntity의 id 사용)
                    String jwtToken = jwtUtils.generateToken(user.getId(), keystoneToken);
                    
                    // JWT 만료 시간 계산
                    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(86400); // 24시간 기본값
                    
                    // DB에 토큰 저장
                    UserTokenEntity tokenEntity = UserTokenEntity.create(user, jwtToken, keystoneToken, expiresAt);
                    userTokenRepositoryPort.save(tokenEntity);
                    
                    return jwtToken;
                });
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
        UserEntity user = userRepositoryPort.findByUserId(userId).orElse(null);
        if (user != null) {
            userTokenRepositoryPort.deactivateAllByUserId(user.getId());
        }
    }

    public String getKeystoneTokenFromJwt(String jwtToken) {
        return userTokenRepositoryPort.findByJwtTokenAndIsActiveTrue(jwtToken)
                .map(UserTokenEntity::getKeystoneToken)
                .orElse(null);
    }

    public Mono<ProjectPermission> getProjectPermission(String projectId , Long userIdx) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userIdx)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String KeystoneToken = userToken.getKeystoneToken();
        return keystoneModule.getPermission(KeystoneToken , projectId);
    }

    public String issueKeystoneToken() {
        return keystoneModule.issueKeystoneToken();
    }
}
