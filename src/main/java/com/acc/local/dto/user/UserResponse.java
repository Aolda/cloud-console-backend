package com.acc.local.dto.user;

import com.acc.server.local.domain.enums.UserStatus;
import com.acc.server.local.domain.model.User;

import java.time.LocalDateTime;

public record UserResponse(
        String keycloakUserId,
        String username,
        String email,
        boolean emailVerified,
        String firstName,
        String lastName,
        boolean enabled,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getKeycloakUserId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}