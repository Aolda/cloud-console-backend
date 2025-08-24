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
public class AuthModule {
    //global
    private final JwtUtils jwtUtils;
    //repository
    private final UserTokenRepositoryPort userTokenRepositoryPort;
    //module
    private final KeystoneModule keystoneModule;
    private final WebClient keystoneWebClient;
    private final OpenStackProperties openStackProperties;

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
}
