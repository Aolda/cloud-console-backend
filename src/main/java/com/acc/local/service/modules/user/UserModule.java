package com.acc.local.service.modules.user;

import com.acc.global.properties.KeycloakProperties;
import com.acc.local.dto.user.UserCreateRequest;
import com.acc.local.dto.user.UserResponse;
import com.acc.server.local.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserModule {
    
    private final WebClient keycloakWebClient;
    private final KeycloakProperties keycloakProperties;

    public UserResponse createUser(String bearerToken, UserCreateRequest request) {
        // 1. 도메인 모델 생성 (PENDING 상태)
        User user = User.createPending(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
        );

        // 2. Keycloak API 호출을 위한 요청 구성
        Map<String, Object> keycloakUserRequest = buildKeycloakUserRequest(request);
        
        // 3. Keycloak 사용자 생성
        String result = createKeycloakUser(bearerToken, keycloakUserRequest);
        
        // 4. 도메인 모델에 Keycloak 정보 할당  -> 생각해보니 로그인 통합 부터 진행했어야함 ;;;
        user.assignKeycloakInfo("1234", false, request.getEnabled());
        
        // 5. 응답 반환
        return UserResponse.from(user);
    }

    private Map<String, Object> buildKeycloakUserRequest(UserCreateRequest request) {
        Map<String, Object> userRequest = Map.of(
                "username", request.getUsername(),
                "email", request.getEmail(),
                "enabled", request.getEnabled(),
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", request.getTemporary()
                ))
        );
        
        log.info("Keycloak 사용자 생성 요청: username={}, email={}", request.getUsername(), request.getEmail());
        return userRequest;
    }

    private String createKeycloakUser(String bearerToken, Map<String, Object> userRequest) {
        try {
            return keycloakWebClient.post()
                    .uri("/admin/realms/{realm}/users", keycloakProperties.getRealm())
                    .header("Authorization", bearerToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(userRequest)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            log.info("Keycloak 사용자 생성 성공: status={}", response.statusCode());
                            return Mono.just("SUCCESS");
                        } else {
                            log.error("Keycloak 사용자 생성 실패: status={}", response.statusCode());
                            return response.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Keycloak 오류 응답: {}", errorBody);
                                        return Mono.error(new RuntimeException("이따가 익셉션 푸시 되면다시" + errorBody));
                                    });
                        }
                    })
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Keycloak API 호출 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("이따가 익셉션 커밋되면 다시 .", e);
        }
    }
}