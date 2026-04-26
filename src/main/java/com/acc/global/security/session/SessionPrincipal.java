package com.acc.global.security.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Keycloak OIDC 세션 기반 인증의 SecurityContext principal.
 *
 * [대체 대상] JwtInfo (JWT 기반 principal)
 *   - 기존: @AuthenticationPrincipal JwtInfo jwtInfo → jwtInfo.getUserId()
 *   - 신규: @AuthenticationPrincipal SessionPrincipal principal → principal.getKeystoneUserId()
 *
 * [설계 원칙]
 *   - 자주 필요한 최소 정보만 담는다 (세션 조회 없이 사용 가능한 것들)
 *   - 상세 정보(UserInfo, Token)는 SessionModule을 통해 Redis에서 조회
 *   - keystoneUserId를 포함한 이유: 대부분의 OpenStack API 호출에 필요하기 때문
 */
@Getter
@RequiredArgsConstructor
public class SessionPrincipal {

    private final String sessionId;
    private final String keycloakUserId;

    /**
     * user_detail.user_id (PK) = Keystone 사용자 UUID.
     * OpenStack API 호출, 권한 조회 등에 사용.
     * 기존 JwtInfo.getUserId()와 동일한 역할.
     */
    private final String keystoneUserId;
}
