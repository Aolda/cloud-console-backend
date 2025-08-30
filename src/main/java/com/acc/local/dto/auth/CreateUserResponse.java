package com.acc.local.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserResponse {
    private String userId;
    private String defaultProjectId;
    private String domainId;
    private String email;
    private boolean enabled;
}
