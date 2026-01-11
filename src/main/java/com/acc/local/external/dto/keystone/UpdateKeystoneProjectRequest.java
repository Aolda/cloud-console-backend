package com.acc.local.external.dto.keystone;


import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record UpdateKeystoneProjectRequest(
        String name,
        Boolean isDomain,
        String description,
        String domainId,
        Boolean enabled,
        List<String> tags
) {
    public Map<String, Object> toKeystoneRequest() {
        Map<String, Object> projectObject = new HashMap<>();

        if (name() != null) {
            projectObject.put("name", name());
        }
        if (description() != null) {
            projectObject.put("description", description());
        }
        if (domainId() != null) {
            projectObject.put("domain_id", domainId());
        }
        if (enabled() != null) {
            projectObject.put("enabled", enabled());
        }
        if (isDomain() != null) {
            projectObject.put("is_domain", isDomain());
        }
        if (tags() != null) {
            projectObject.put("tags", tags());
        }

        Map<String, Object> request = new HashMap<>();
        request.put("project", projectObject);

        return request;
    }
}
