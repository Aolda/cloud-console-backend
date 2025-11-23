package com.acc.local.domain.model.auth;

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
                .name(request.userName())
                .email(request.userEmail())
                .enabled(true)
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
                .build();
    }

    public static KeystoneUser from(SignupRequest request) {
        return KeystoneUser.builder()
                .name(request.email()) // email을 name(아이디)로 사용
                .email(request.email())
                .password(request.password())
                .enabled(true)
                .build();
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
