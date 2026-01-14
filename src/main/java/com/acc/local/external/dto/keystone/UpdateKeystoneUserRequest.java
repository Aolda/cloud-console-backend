package com.acc.local.external.dto.keystone;

import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record UpdateKeystoneUserRequest(
        String email,
        String password,
        String description,
        String defaultProjectId,
        Boolean isEnable
) {
    public Map<String, Object> toKeystoneRequest() {
        Map<String, Object> userObject = new HashMap<>();

        if (email() != null) {
            userObject.put("name", extractUsernameFromEmail(email()));
            userObject.put("email", email());
        }
        if (password() != null) {
            userObject.put("password", password());
        }
        if (description() != null) {
            userObject.put("description", description());
        }
        if (defaultProjectId() != null) {
            userObject.put("default_project_id", defaultProjectId());
        }
        userObject.put("enabled", isEnable());

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
