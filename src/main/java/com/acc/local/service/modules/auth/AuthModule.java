package com.acc.local.service.modules.auth;
import com.acc.global.properties.OpenStackProperties;
import com.acc.global.security.JwtUtils;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
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
    public Mono<String> generateJwtWithKeystoneToken(String userId, String keycloakToken) {
        // 기존 사용자 토큰 비활성화
        userTokenRepositoryPort.deactivateAllByUserId(userId);
        
        // Keystone Federate Authentication으로 사용자 토큰 발급 후 JWT 생성
        return keystoneModule.login(keycloakToken)
                .map(keystoneToken -> {
                    // JWT 토큰 생성
                    String jwtToken = jwtUtils.generateToken(userId, keystoneToken);
                    
                    // JWT 만료 시간 계산
                    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(86400); // 24시간 기본값
                    
                    // DB에 토큰 저장
                    UserTokenEntity tokenEntity = UserTokenEntity.create(userId, jwtToken, keystoneToken, expiresAt);
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
        userTokenRepositoryPort.deactivateAllByUserId(userId);
    }

    public String getKeystoneTokenFromJwt(String jwtToken) {
        return userTokenRepositoryPort.findByJwtTokenAndIsActiveTrue(jwtToken)
                .map(UserTokenEntity::getKeystoneToken)
                .orElse(null);
    }
}
