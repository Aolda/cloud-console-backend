package com.acc.server.openstack.adapter.out;

import com.acc.server.openstack.domain.port.KeystonePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeystoneRestAdapter implements KeystonePort {
    private final WebClient keystoneWebClient;

    @Override
    public String issueToken(String username, String password, String projectName) {
        Map<String,Object> body = Map.of(
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
                .bodyValue(body)
                .exchangeToMono(resp ->
                        Mono.just(resp.headers().asHttpHeaders().getFirst("X-Subject-Token")))
                .block();  // 실서비스에선 timeout·에러 핸들링 추가
    }
}
