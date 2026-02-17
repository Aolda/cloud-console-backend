package com.acc.local.external.dto.keycloak;

import org.springframework.util.MultiValueMap;

/**
 * Keycloak OIDC 토큰 엔드포인트는 application/x-www-form-urlencoded 만 허용합니다.
 *
 * Spring WebFlux에서 form data를 보내는 표준 방식이:
 * {@code BodyInserters.fromFormData(MultiValueMap<String, String>)}
 * 이 메서드의 시그니처가 MultiValueMap을 요구하기 때문에, Request DTO에서 toFormData()로 변환하는 패턴 사용
 */
public interface KeycloakFormRequest {
    MultiValueMap<String, String> toFormData();
}
