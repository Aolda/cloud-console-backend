package com.acc.local.service.adapters.auth;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.KeystoneModule;
import com.acc.local.service.ports.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Primary
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
    private final AuthModule authModule;


    @Override
    public String issueKeystoneToken() {
        return authModule.issueKeystoneToken();
    }

    // keycloak 로그인 이후 redirect URL 엔드포인트에서 사용될 메서드
    @Override
    public Mono<String> authenticateAndGenerateJwt(String userId, String keycloakToken) {
        return authModule.generateJwtWithKeystoneToken(userId, keycloakToken);
    }

    // 권한 부여 전, 토큰 검증 메서드
    @Override
    public boolean validateJwt(String jwtToken) {
        return authModule.validateJwtToken(jwtToken);
    }

    @Override
    public void invalidateUserTokens(String userId) {
        authModule.invalidateUserTokens(userId);
    }


    @Override
    public Mono<ProjectPermission> getProjectPermission(String projectId, Long userIdx) {
        return authModule.getProjectPermission(projectId, userIdx);
    }
}
