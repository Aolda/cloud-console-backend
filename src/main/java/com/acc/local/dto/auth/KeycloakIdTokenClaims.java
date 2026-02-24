package com.acc.local.dto.auth;

/**
 * Keycloak ID Token에서 추출한 클레임 집합.
 *
 * Keycloak Admin Console에서 아래 Mapper를 클라이언트에 설정해야 한다:
 *   - department  : User Attribute → Token Claim Name "department"  (String)
 *   - studentId   : User Attribute → Token Claim Name "studentId"   (String)
 *   - email, preferred_username은 표준 클레임으로 openid scope에 기본 포함
 */
public record KeycloakIdTokenClaims(
        String subject,           // sub    → keycloakUserId (Keycloak 내부 UUID)
        String email,             // email  → 계정 연결/조회 기준
        String department,        // custom → UserIdentityEntity.department
        String studentId,         // custom → UserIdentityEntity.studentId
        String preferredUsername  // preferred_username → 신규 가입 시 userName 초기값
) {}
