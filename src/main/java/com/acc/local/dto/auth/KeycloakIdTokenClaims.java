package com.acc.local.dto.auth;

/**
 * Keycloak ID Token에서 추출한 클레임 집합.
 *
 * Keycloak Admin Console에서 아래 Mapper를 클라이언트에 설정해야 한다:
 *   - ajou_major      : User Attribute → Token Claim Name "ajou_major"      (String)
 *   - ajou_status     : User Attribute → Token Claim Name "ajou_status"     (String)
 *   - ajou_grade      : User Attribute → Token Claim Name "ajou_grade"      (String)
 *   - ajou_student_id : User Attribute → Token Claim Name "ajou_student_id" (String, SPI 연동 시)
 *   - auth_idp_type   : User Attribute → Token Claim Name "auth_idp_type"   (String, google/gitlab)
 *   - email, preferred_username은 표준 클레임으로 openid scope에 기본 포함
 */
public record KeycloakIdTokenClaims(
        String subject,           // sub              → keycloakUserId (Keycloak 내부 UUID)
        String email,             // email            → 계정 연결/조회 기준
        String preferredUsername, // preferred_username → 신규 가입 시 userName 초기값
        String ajouMajor,         // ajou_major       → "소프트웨어및컴퓨터공학전공"
        String ajouStatus,        // ajou_status      → "SS0001(학생(학부))"
        String ajouGrade,         // ajou_grade       → "4"
        String ajouStudentId,     // ajou_student_id  → 학번 (Keycloak SPI 연동 시 채워짐)
        String authIdpType        // auth_idp_type     → IDP 종류 (google/gitlab)
) {}
