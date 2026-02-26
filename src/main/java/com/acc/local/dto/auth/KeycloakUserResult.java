package com.acc.local.dto.auth;

/**
 * KeycloakUserModule.findOrRegisterKeycloakUser() 반환값.
 *
 * 이 결과를 바탕으로 KeycloakAuthModule이:
 *   1. Keystone unscoped token 발급  (keystoneUsername + keystonePlainPassword)
 *   2. SessionData 구성              (keystoneUserId, userName)
 * 을 수행한다.
 */
public record KeycloakUserResult(
        String keystoneUserId,        // user_detail.user_id (PK) = Keystone 사용자 UUID
        String keystoneUsername,      // Keystone 로그인 name (email prefix, e.g. "guswp320")
        String keystonePlainPassword, // 복호화된 평문 패스워드 (Keystone unscoped token 발급용)
        String userName               // user_detail.user_name → SessionData.UserInfo.name
) {}
