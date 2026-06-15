package com.acc.local.dto.auth;

import java.util.List;

/**
 * Keycloak ID Token에서 추출한 클레임 집합.
 *
 * Keycloak Admin Console에서 아래 Mapper를 클라이언트에 설정해야 한다:
 *   - ajou_major        : User Attribute → Token Claim Name "ajou_major"        (String)
 *   - ajou_status       : User Attribute → Token Claim Name "ajou_status"       (String)
 *   - ajou_grade        : User Attribute → Token Claim Name "ajou_grade"        (String)
 *   - ajou_student_id   : User Attribute → Token Claim Name "ajou_student_id"   (String, SPI 연동 시)
 *   - user_phone_number : User Attribute → Token Claim Name "user_phone_number" (String, SPI 연동 시)
 *   - auth_idp_type     : User Attribute → Token Claim Name "auth_idp_type"     (String, google/gitlab)
 *   - groups            : Group Membership → Token Claim Name "groups", Full group path ON
 *   - email, preferred_username은 표준 클레임으로 openid scope에 기본 포함
 *   - given_name, family_name은 표준 profile scope에 포함
 *   - ajou_firstName, ajou_lastName은 표준 이름 클레임이 없을 때 표시 이름 fallback으로 사용
 *   - name은 Keycloak full name mapper가 생성한 값으로, 성/이름 조합이 없을 때만 fallback으로 사용
 */
public record KeycloakIdTokenClaims(
        String subject,           // sub                → keycloakUserId (Keycloak 내부 UUID)
        String email,             // email              → 계정 연결/조회 기준
        String preferredUsername, // preferred_username → 신규 가입 시 userName 초기값
        String name,              // name               → Keycloak 표시 이름
        String givenName,         // given_name         → 이름
        String familyName,        // family_name        → 성
        String ajouMajor,         // ajou_major         → "소프트웨어및컴퓨터공학전공"
        String ajouStatus,        // ajou_status        → "SS0001(학생(학부))"
        String ajouGrade,         // ajou_grade         → "4"
        String ajouStudentId,     // ajou_student_id    → 학번 (Keycloak SPI 연동 시 채워짐)
        String phoneNumber,       // user_phone_number  → 핸드폰 번호 (Keycloak SPI 연동 시 채워짐)
        String authIdpType,       // auth_idp_type      → IDP 종류 (google/gitlab)
        List<String> groups       // groups             → Keycloak 그룹 전체 경로 목록 (e.g. ["/Ajou_Univ/Aolda_Admin"])
) {
    public String displayName() {
        if (familyName != null && !familyName.isBlank() && givenName != null && !givenName.isBlank()) {
            return familyName + givenName;
        }
        if (givenName != null && !givenName.isBlank()) {
            return givenName;
        }
        if (familyName != null && !familyName.isBlank()) {
            return familyName;
        }
        if (name != null && !name.isBlank()) {
            return name;
        }
        return preferredUsername;
    }
}
