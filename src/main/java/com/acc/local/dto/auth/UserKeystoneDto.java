package com.acc.local.dto.auth;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
public record UserKeystoneDto(
        String id,
        String name,
        String password,
        String domainId,
        String defaultProjectId,
        boolean enabled,
        List<Map<String, Object>>federated,
        Map<String, String> links,
        String passwordExpiresAt,
        String email,
        String description,
        Map<String, Object> options
) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserKeystoneDto UserKeystoneDto = (UserKeystoneDto) obj;
        return Objects.equals(id, UserKeystoneDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
