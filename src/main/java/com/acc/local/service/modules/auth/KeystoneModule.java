package com.acc.local.service.modules.auth;

import com.acc.global.properties.OpenStackProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeystoneModule {

    private final WebClient keystoneWebClient;
    private final OpenStackProperties openStackProperties;

    //  Modified: 어떤 토큰인지 분리하기 위해서 issueToken -> issueKeystoneToken 으로 변경
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
                .uri("/identity/v3/auth/tokens")
                .bodyValue(request)
                .exchangeToMono(resp -> {
                    String token = resp.headers().asHttpHeaders().getFirst("X-Subject-Token");
                    return Mono.justOrEmpty(token);
                })
                .block();
    }

    public String login(String keycloakCode) {
        // TODO: 화균님이 구현 예정
        // Keystone Federate Authentication 로직 (keycloak code로 로그인)
        return null;
    }

    public Map<String, Object> getPermission(Object user) {
        // TODO: 다른 분이 구현 예정 (Phase2에서는 KeycloakModule로 이동)
        // Keystone API로 사용자 권한 조회 로직
        // 1. JWT에서 추출한 keystone token 사용
        // 2. Keystone API 호출하여 프로젝트별 권한 조회  
        // 3. Map<String, ProjectPermission> 형태로 반환
        return null;
    }
}
