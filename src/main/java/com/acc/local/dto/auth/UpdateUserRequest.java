package com.acc.local.dto.auth;

import lombok.Getter;
import lombok.Setter;

public record UpdateUserRequest(
        String userName,
        String userEmail,
        String description,
        Boolean enabled,
        String defaultProjectId
) { }