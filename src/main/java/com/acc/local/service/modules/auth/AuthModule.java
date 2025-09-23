package com.acc.local.service.modules.auth;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.global.properties.OpenStackProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.acc.global.security.JwtUtils;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final WebClient keystoneWebClient;
    private final OpenStackProperties openStackProperties;
    private final JwtUtils jwtUtils;
    private final UserTokenRepositoryPort userTokenRepositoryPort;
    private final KeystoneModule keystoneModule;

    @Deprecated // TODO: AuthController 측 의존성문제 해결 시 삭제예정
    public String issueKeystoneToken() {
        String username = openStackProperties.getKeystone().getUsername();
        String password = openStackProperties.getKeystone().getPassword();
        String projectName = openStackProperties.getKeystone().getProject();

        Map<String, Object> request = Map.of(
                "auth", Map.of(
                        "identity", Map.of(
                                "methods", List.of("password"),
                                "password", Map.of("user", Map.of(
                                        "name", username,
                                        "domain", Map.of("name", "default"),
                                        "password", password
                                ))
                        ),
                        "scope", Map.of("project", Map.of(
                                "name", projectName,
                                "domain", Map.of("name", "default")
                        ))
                )
        );

        return keystoneWebClient.post()
                .uri("/v3/auth/tokens")
                .bodyValue(request)
                .exchangeToMono(resp -> {
                    String token = resp.headers().asHttpHeaders().getFirst("X-Subject-Token");
                    return Mono.justOrEmpty(token);
                })
                .block();
    }
    public String getProjectIdFromToken(String token) {
        JsonNode tokenInfo = keystoneWebClient.get()
                .uri("/identity/v3/auth/tokens")
                .header("X-Auth-Token", token)
                .header("X-Subject-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return tokenInfo.path("token").path("project").path("id").asText();
    }
    public List<String> getRolesFromToken(String token) {
        JsonNode tokenInfo = keystoneWebClient.get()
                .uri("/identity/v3/auth/tokens")
                .header("X-Auth-Token", token)
                .header("X-Subject-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode rolesNode = tokenInfo.path("token").path("roles");

        List<String> roles = new java.util.ArrayList<>();
        if (rolesNode.isArray()) {
            for (JsonNode role : rolesNode) {
                roles.add(role.path("name").asText());
            }
        }
        return roles;
    }

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

    public ProjectPermission getProjectPermission(String projectId, String userId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneToken();
        return keystoneModule.getPermission(keystoneToken, projectId);
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
}
