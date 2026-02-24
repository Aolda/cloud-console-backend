package com.acc.local.external.dto.keystone;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record CreateKeystoneUserRequest(
        String email,
        String password,
        Boolean isEnable
) {
    public Map<String, Object> toKeystoneRequest() {
        Map<String, Object> userObject = new HashMap<>();

        // Skyline에서 @를 도메인으로 인식하는 문제로 email prefix(@앞부분)만 사용
        userObject.put("name", extractUsernameFromEmail(email()));
        userObject.put("password", password());
        userObject.put("enabled", isEnable());
        userObject.put("email", email());

        Map<String, Object> request = new HashMap<>();
        request.put("user", userObject);

        return request;
    }

    /**
     * 이메일에서 username 부분(@앞부분)만 추출
     * Skyline에서 @를 도메인으로 인식하는 문제 해결용
     */
    private static String extractUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.substring(0, email.indexOf("@"));
    }
}
