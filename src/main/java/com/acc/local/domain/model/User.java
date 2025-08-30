package com.acc.local.domain.model;

import com.acc.local.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 사용자 도메인 모델
 * - Keycloak cloud-service realm 전용
 * - 핵심 필드만 관리하고 나머지는 필요시 Keycloak API 직접 호출
 * - KeycloakUserId
 */
@Getter
@Setter
@Builder
public class User {

    // 핵심 필드들 (필수)
    private final String keycloakUserId;            // Keycloak에서 생성 후 받은 사용자 ID
    private String userName;                  // 로그인 ID
    private String userPassword;
    private String email;                     // 이메일
    private String firstName;                 // 이름
    private String lastName;                  // 성
    private boolean enabled;                  // 활성화 상태
    private UserStatus status;                // 백엔드 상태 관리
    private Map<String, List<String>> attributes; // 역할 저장 예시: "cloud-service" -> ["GROOT", "PROJECT_A_ROOT", "PROJECT_B_IAM_CTRL"]




    public List<String> getAttribute(String key) {
        return this.attributes.get(key);
    }

    public Map<String, List<String>> getAttributes() {
        return new HashMap<>(attributes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(keycloakUserId, user.keycloakUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keycloakUserId);
    }

}
