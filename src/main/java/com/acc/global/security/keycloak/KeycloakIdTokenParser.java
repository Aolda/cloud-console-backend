package com.acc.global.security.keycloak;

import com.acc.global.exception.auth.KeycloakErrorCode;
import com.acc.global.exception.auth.KeycloakException;
import com.acc.local.dto.auth.KeycloakIdTokenClaims;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Keycloak ID Token (JWT) 파싱 컴포넌트.
 *
 * [위치 근거] JwtUtils가 global/security/jwt/ 에 있는 것처럼,
 *            ID Token 파싱도 JWT 파싱 관심사이므로 global/security/ 계층에 위치한다.
 *
 * [서명 검증 생략 이유]
 *   Keycloak Authorization Server로부터 HTTPS로 직접 수신한 토큰이므로
 *   중간자 공격 가능성이 없다. Payload Base64 디코딩만 수행한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakIdTokenParser {

    private final ObjectMapper objectMapper;

    /**
     * ID Token JWT의 payload를 Base64 디코딩하여 sub (keycloakUserId) 추출
     */
    public String extractSubject(String idToken) {
        try {
            JsonNode payload = decodePayload(idToken);

            String sub = payload.get("sub").asText();
            if (sub == null || sub.isBlank()) {
                throw new IllegalArgumentException("sub claim is missing or empty");
            }

            return sub;
        } catch (Exception e) {
            log.error("Keycloak ID Token 파싱 실패", e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_ID_TOKEN_PARSE_FAILED, e);
        }
    }

    /**
     * ID Token에서 모든 필요 클레임을 한 번에 추출한다.
     *
     * 추출 클레임:
     *   - sub               : Keycloak 사용자 UUID (keycloakUserId)
     *   - email             : 사용자 이메일
     *   - preferred_username: Keystone username과 동일한 email prefix
     *   - department        : 커스텀 클레임 (Keycloak Mapper 설정 필요)
     *   - studentId         : 커스텀 클레임 (Keycloak Mapper 설정 필요)
     */
    public KeycloakIdTokenClaims extractClaims(String idToken) {
        try {
            JsonNode payload = decodePayload(idToken);

            String sub = getRequiredClaim(payload, "sub");
            String email = getRequiredClaim(payload, "email");
            String preferredUsername = getOptionalClaim(payload, "preferred_username", email.split("@")[0]);
            String department = getOptionalClaim(payload, "department", "");
            String studentId = getOptionalClaim(payload, "studentId", "");

            return new KeycloakIdTokenClaims(sub, email, department, studentId, preferredUsername);
        } catch (KeycloakException e) {
            throw e;
        } catch (Exception e) {
            log.error("Keycloak ID Token 클레임 추출 실패", e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_ID_TOKEN_PARSE_FAILED, e);
        }
    }

    private JsonNode decodePayload(String idToken) throws Exception {
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format");
        }
        byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
        return objectMapper.readTree(decodedPayload);
    }

    private String getRequiredClaim(JsonNode payload, String claimName) {
        JsonNode node = payload.get(claimName);
        if (node == null || node.isNull() || node.asText().isBlank()) {
            throw new IllegalArgumentException("Required claim missing: " + claimName);
        }
        return node.asText();
    }

    private String getOptionalClaim(JsonNode payload, String claimName, String defaultValue) {
        JsonNode node = payload.get(claimName);
        if (node == null || node.isNull() || node.asText().isBlank()) {
            return defaultValue;
        }
        return node.asText();
    }
}
