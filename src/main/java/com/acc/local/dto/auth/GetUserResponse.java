package com.acc.local.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class GetUserResponse {
    private String id;
    private String name;
    private String domainId;
    private String defaultProjectId;
    private boolean enabled;
    private List<Map<String, Object>> federated;
    private Map<String, String> links;
    private String passwordExpiresAt;
    private String email;
    private String description;
    private Map<String, Object> options;
}