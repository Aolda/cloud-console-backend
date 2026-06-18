package com.acc.global.security.keycloak;

import com.acc.local.dto.auth.KeycloakIdTokenClaims;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeycloakIdTokenParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KeycloakIdTokenParser parser = new KeycloakIdTokenParser(objectMapper);

    @Test
    void displayNameUsesFamilyAndGivenNameBeforeKeycloakFullName() throws Exception {
        Map<String, Object> payload = basePayload();
        payload.put("name", "현제 이");
        String idToken = token(payload);

        KeycloakIdTokenClaims claims = parser.extractClaims(idToken);

        assertEquals("이현제", claims.displayName());
    }

    @Test
    void displayNameFallsBackToFamilyAndGivenName() throws Exception {
        String idToken = token(basePayload());

        KeycloakIdTokenClaims claims = parser.extractClaims(idToken);

        assertEquals("이현제", claims.displayName());
    }

    @Test
    void displayNameFallsBackToAjouNameAttributes() throws Exception {
        Map<String, Object> payload = basePayload();
        payload.remove("given_name");
        payload.remove("family_name");
        payload.put("ajou_firstName", "현제");
        payload.put("ajou_lastName", "이");
        String idToken = token(payload);

        KeycloakIdTokenClaims claims = parser.extractClaims(idToken);

        assertEquals("이현제", claims.displayName());
    }

    private String token(Map<String, Object> payload) throws Exception {
        String header = encode(Map.of("alg", "none"));
        return header + "." + encode(payload) + ".";
    }

    private Map<String, Object> basePayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", "keycloak-user-id");
        payload.put("email", "hyeonje@example.com");
        payload.put("preferred_username", "hyeonje");
        payload.put("given_name", "현제");
        payload.put("family_name", "이");
        payload.put("ajou_major", "소프트웨어및컴퓨터공학전공");
        payload.put("ajou_status", "SS0001(학생(학부))");
        payload.put("ajou_grade", "1");
        payload.put("ajou_student_id", "202012345");
        payload.put("user_phone_number", "010-0000-0000");
        payload.put("auth_idp_type", "google");
        return payload;
    }

    private String encode(Object value) throws Exception {
        byte[] json = objectMapper.writeValueAsString(value).getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
    }
}
