package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.CreateRoleRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class Role {

    private String id;
    private String name;
    private String description;
    private String domainId;
    private Map<String, String> links;
    private Map<String, Object> options;

    public static Role from(CreateRoleRequest request) {
        if (request == null) {
            return null;
        }

        return Role.builder()
                .name(request.name())
                .description(request.description())
                .domainId(request.domainId())
                .build();
    }
}