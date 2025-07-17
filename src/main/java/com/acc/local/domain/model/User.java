package com.acc.local.domain.model;

import com.acc.local.domain.enums.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 사용자 도메인 모델
 * - Keycloak cloud-service realm 전용
 * - 핵심 필드만 관리하고 나머지는 필요시 Keycloak API 직접 호출
 * - 생성 플로우: 1) createPending으로 PENDING 상태 생성 → 2) Keycloak API 호출 → 3) assignKeycloakInfo로 정보 할당 및 ACTIVE 상태 전환
 */
@Getter
public class User {

    // 핵심 필드들 (필수)
    private String keycloakUserId;            // Keycloak에서 생성 후 받은 사용자 ID
    private String username;                  // 로그인 ID
    private String email;                     // 이메일
    private boolean emailVerified;            // 이메일 인증 여부
    private String firstName;                 // 이름
    private String lastName;                  // 성
    private boolean enabled;                  // 활성화 상태
    private UserStatus status;                // 백엔드 상태 관리
    // 확장 가능한 속성 관리
    private Map<String, List<String>> attributes;   // 모든 커스텀 속성 (역할, 부서, 직급 등)
                                                     // 역할 저장 예시: "cloud-service" -> ["GROOT", "PROJECT_A_ROOT", "PROJECT_B_IAM_CTRL"]

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // PENDING 상태 사용자 생성용 생성자
    private User(String username, String email, String firstName, String lastName) {
        validateUsername(username);
        validateEmail(email);

        this.keycloakUserId = null;
        this.username = username;
        this.email = email;
        this.emailVerified = false;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = true;
        this.status = UserStatus.PENDING;
        this.attributes = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 팩토리 메서드

    // 메인 플로우: PENDING 상태 사용자 생성
    public static User createPending(String username, String email, String firstName, String lastName) {
        return new User(username, email, firstName, lastName);
    }

    // Keycloak 생성 완료 후 정보 할당
    public void assignKeycloakInfo(String keycloakUserId, boolean emailVerified, boolean enabled) {
        if (this.keycloakUserId != null) {
            throw new IllegalStateException("Keycloak 사용자 정보가 이미 할당되었습니다.");
        }
        validateKeycloakUserId(keycloakUserId);
        this.keycloakUserId = keycloakUserId;
        this.emailVerified = emailVerified;
        this.enabled = enabled;  // Keycloak 상태 반영
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 로직
    public void updateBasicInfo(String email, boolean emailVerified, String firstName,
                                String lastName, boolean enabled) {
        this.email = email;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    // 상태변환 로직들
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canAccessProject() {
        return this.status == UserStatus.ACTIVE && this.enabled && isKeycloakCreated();
    }


    public boolean isKeycloakCreated() {
        return this.keycloakUserId != null;
    }

    // 커스텀 속성 관리
    public void addAttribute(String key, List<String> values) {
        this.attributes.put(key, values);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getAttribute(String key) {
        return this.attributes.get(key);
    }

    // 검증 로직
    private void validateKeycloakUserId(String keycloakUserId) {
        if (keycloakUserId == null || keycloakUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Keycloak 사용자 ID는 필수입니다.");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("유효한 이메일 주소여야 합니다.");
        }
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
