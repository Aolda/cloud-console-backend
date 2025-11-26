package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.AdminCreateUserRequest;
import com.acc.local.dto.auth.CreateUserRequest;
import com.acc.local.dto.auth.SignupRequest;
import com.acc.local.dto.auth.UpdateUserRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Map;
import java.util.List;

/**
 *  KeyStone 의 사용자 객체에 대한 도메인
 */
@Getter
@Setter
@Builder
public class KeystoneUser {

    // Keystone 필드
    private String id;
    private String name;
    private String password;
    private String domainId;
    private String defaultProjectId;
    private boolean enabled;
    private List<Map<String, Object>> federated;
    private Map<String, String> links;
    private String passwordExpiresAt;
    private String email;
    private String description;
    private Map<String, Object> options;


    public static KeystoneUser from(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        return KeystoneUser.builder()
                .name(extractUsernameFromEmail(request.userName()))
                .email(request.userEmail())
                .enabled(true)
                // ACC 내부 정보

                .build();
    }

    public static KeystoneUser from(UpdateUserRequest request) {
        if (request == null) {
            return null;
        }

        return KeystoneUser.builder()
                .name(request.userName())
                .email(request.userEmail())
                .description(request.description())
                .defaultProjectId(request.defaultProjectId())
                .enabled(request.enabled() != null ? request.enabled() : true)
                // ACC 내부 정보
                .build();
    }

    public static KeystoneUser from(SignupRequest request) {
        return KeystoneUser.builder()
                .name(extractUsernameFromEmail(request.email())) // email의 @ 앞부분만 name(아이디)로 사용
                .email(request.email())
                .password(request.password())
                .enabled(true)
                .build();
    }

    public static KeystoneUser from(AdminCreateUserRequest request) {
        return KeystoneUser.builder()
                .name(extractUsernameFromEmail(request.email())) // email의 @ 앞부분만 name(아이디)로 사용
                .password(request.password())
                .enabled(request.isEnabled())
                .build();
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeystoneUser keystoneUser = (KeystoneUser) obj;
        return Objects.equals(id, keystoneUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
