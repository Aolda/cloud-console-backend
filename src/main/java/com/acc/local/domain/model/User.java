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
 *  KeyStone 의 사용자 객체에 대한 도메인
 */
@Getter
@Setter
@Builder
public class User {

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



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
